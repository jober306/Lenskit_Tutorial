package lenskit.tutorial;

import java.io.File;
import java.util.List;

import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.RatingPredictor;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.baseline.BaselineScorer;
import org.grouplens.lenskit.baseline.ItemMeanRatingItemScorer;
import org.grouplens.lenskit.baseline.UserMeanBaseline;
import org.grouplens.lenskit.baseline.UserMeanItemScorer;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommender;
import org.grouplens.lenskit.data.dao.EventDAO;
import org.grouplens.lenskit.data.text.TextEventDAO;
import org.grouplens.lenskit.knn.item.ItemItemScorer;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer;

public class TutorialDriver {

	public static void main(String[] args) {
		LenskitConfiguration conf = new LenskitConfiguration();
		initializeRecommendersConfig(conf);
		initializeDataConfig(conf);
		LenskitRecommender rec = buildRecommender(conf);
		testItemRecommender(rec);
		testRatingPredictor(rec);
	}

	private static void initializeRecommendersConfig(LenskitConfiguration conf) {
		conf.bind(ItemScorer.class).to(ItemItemScorer.class);
		conf.bind(ItemScorer.class).withQualifier(BaselineScorer.class)
				.to(UserMeanItemScorer.class);
		conf.bind(ItemScorer.class).withQualifier(UserMeanBaseline.class)
				.to(ItemMeanRatingItemScorer.class);
		conf.bind(UserVectorNormalizer.class).to(
				BaselineSubtractingUserVectorNormalizer.class);
	}

	private static void initializeDataConfig(LenskitConfiguration conf) {
		File ratingsFile = new File(TutorialDriver.class.getClassLoader()
				.getResource("ratings.csv").getFile());
		TextEventDAO dao = TextEventDAO.ratings(ratingsFile, ",");
		conf.bind(EventDAO.class).to(dao);
	}

	private static LenskitRecommender buildRecommender(LenskitConfiguration conf) {
		try {
			return LenskitRecommender.build(conf);
		} catch (RecommenderBuildException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void testItemRecommender(LenskitRecommender rec) {
		ItemRecommender irec = rec.getItemRecommender();
		List<ScoredId> recommendations = irec.recommend(42, 10);
		recommendations.stream().map(TutorialDriver::formatScoredId)
				.forEach(System.out::println);
	}

	private static void testRatingPredictor(LenskitRecommender rec) {
		RatingPredictor pred = rec.getRatingPredictor();
		double score = pred.predict(42, 17);
		System.out.println(score);
	}

	private static String formatScoredId(ScoredId scoreId) {
		return "Id: " + scoreId.getId() + "\tScore: " + scoreId.getScore();
	}
}
