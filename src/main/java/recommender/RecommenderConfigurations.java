package recommender;

import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.baseline.BaselineScorer;
import org.grouplens.lenskit.baseline.ItemMeanRatingItemScorer;
import org.grouplens.lenskit.baseline.UserMeanBaseline;
import org.grouplens.lenskit.baseline.UserMeanItemScorer;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.eval.algorithm.AlgorithmInstance;
import org.grouplens.lenskit.iterative.IterationCount;
import org.grouplens.lenskit.iterative.LearningRate;
import org.grouplens.lenskit.knn.item.ItemItemScorer;
import org.grouplens.lenskit.mf.funksvd.FeatureCount;
import org.grouplens.lenskit.mf.funksvd.FunkSVDItemScorer;
import org.grouplens.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer;

public enum RecommenderConfigurations {

	PERS_MEAN_CONF("PersMean", createPersMeanRecommenderConf()), ITEM_ITEM_CONF(
			"ItemItem", createItemItemRecommenderConf()), FUNK_SVD_CONF(
			"FunkSVD", createFunkSVDRecommenderConf());

	private final LenskitConfiguration conf;
	private final String name;

	private RecommenderConfigurations(String name, LenskitConfiguration conf) {
		this.name = name;
		this.conf = conf;
	}

	public LenskitConfiguration getConf() {
		return conf;
	}

	public String getName() {
		return name;
	}

	private static LenskitConfiguration createPersMeanRecommenderConf() {
		LenskitConfiguration conf = new LenskitConfiguration();
		conf.bind(ItemScorer.class).to(UserMeanItemScorer.class);
		conf.bind(ItemScorer.class).withQualifier(UserMeanBaseline.class)
				.to(ItemMeanRatingItemScorer.class);
		return conf;
	}

	private static LenskitConfiguration createItemItemRecommenderConf() {
		LenskitConfiguration conf = new LenskitConfiguration();
		conf.bind(ItemScorer.class).to(ItemItemScorer.class);
		conf.bind(UserVectorNormalizer.class).to(
				BaselineSubtractingUserVectorNormalizer.class);
		conf.within(UserVectorNormalizer.class).bind(ItemScorer.class)
				.withQualifier(BaselineScorer.class)
				.to(ItemMeanRatingItemScorer.class);
		return conf;
	}

	private static LenskitConfiguration createFunkSVDRecommenderConf() {
		LenskitConfiguration conf = new LenskitConfiguration();
		conf.bind(ItemScorer.class).to(FunkSVDItemScorer.class);
		conf.bind(UserVectorNormalizer.class).to(
				BaselineSubtractingUserVectorNormalizer.class);
		conf.bind(ItemScorer.class).withQualifier(BaselineScorer.class)
				.to(UserMeanItemScorer.class);
		conf.bind(ItemScorer.class).withQualifier(UserMeanBaseline.class)
				.to(ItemMeanRatingItemScorer.class);
		conf.set(FeatureCount.class).to(40);
		conf.set(LearningRate.class).to(0.002);
		conf.set(IterationCount.class).to(125);
		return conf;
	}

	public AlgorithmInstance buildInstance() {
		return new AlgorithmInstance(this.getName(), this.getConf());
	}
}
