package lenskit.tutorial;

import static recommender.RecommenderConfigurations.FUNK_SVD_CONF;
import static recommender.RecommenderConfigurations.ITEM_ITEM_CONF;
import static recommender.RecommenderConfigurations.PERS_MEAN_CONF;

import java.io.File;

import org.grouplens.lenskit.data.pref.PreferenceDomain;
import org.grouplens.lenskit.data.source.CSVDataSourceBuilder;
import org.grouplens.lenskit.data.source.DataSource;
import org.grouplens.lenskit.eval.TaskExecutionException;
import org.grouplens.lenskit.eval.metrics.predict.CoveragePredictMetric;
import org.grouplens.lenskit.eval.metrics.predict.NDCGPredictMetric;
import org.grouplens.lenskit.eval.metrics.predict.RMSEPredictMetric;
import org.grouplens.lenskit.eval.traintest.SimpleEvaluator;

public class TutorialEvalDriver {

	static int numberOfFold = 10;

	public static void main(String[] args) throws TaskExecutionException {
		SimpleEvaluator eval = new SimpleEvaluator();
		eval.addDataset(buildDataSource(), numberOfFold);
		eval.addAlgorithm(PERS_MEAN_CONF.buildInstance());
		eval.addAlgorithm(ITEM_ITEM_CONF.buildInstance());
		eval.addAlgorithm(FUNK_SVD_CONF.buildInstance());
		eval.addMetric(CoveragePredictMetric.class);
		eval.addMetric(RMSEPredictMetric.class);
		eval.addMetric(NDCGPredictMetric.class);
		eval.setOutputPath("results.txt");
		eval.call();
	}

	private static DataSource buildDataSource() {
		File ratingsFile = new File("src/test/resources/ratings.csv");
		CSVDataSourceBuilder builder = new CSVDataSourceBuilder(ratingsFile);
		builder.setDomain(new PreferenceDomain(1.0, 5.0, 1.0));
		return builder.build();
	}
}
