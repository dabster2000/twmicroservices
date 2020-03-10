/***
 * The Example is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Example is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Neuroph. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.trustworks.invoicewebui.ai.riskminimizer;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.LMS;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.List;
import java.util.Stack;

/**
 * See http://neuroph.sourceforge.net/tutorials/StockMarketPredictionTutorial.html
 * @author Dr.V.Steinhauer
 */
public class RiverWaves {

    private static final int MaxIterations = 10;
    private double minrfactor = 999999999.0D;
    private int inputsCount = 6;
    private Stack stack = new Stack();
    private double bestpredicted = 0.0D;
    public static final double daxmax = 10000.0D;
    

    public RiverWaves() {
        //TODO set MaxIterations,LearningRate,MaxError
    }


    public double autodetection(double[] y) {
        
        y = TrainingSetImportStock.simpleNormalizing(y, daxmax);        
        for (inputsCount = 2; inputsCount < y.length / 2; inputsCount++) {
            stack = new Stack();
            for (;;) {
                stack = filterLoop(y);
                //System.out.println("Current inputsCount:" + inputsCount + " Global Result -> Removed Elements:");
                //for (Object aBestStack : bestStack) {
                    //System.out.print(aBestStack + " ");
                //}
                //System.out.print(" BEST inputsCount:" + bestinputsCount + " BEST Predicted Value:" + (bestpredicted * daxmax) + " MIN RFACTOR:" + minrfactor + "\n");
                int n = (Integer) stack.lastElement();
                if (n == -1) {
                    break;
                }
            }
        }
        return bestpredicted;
    }

    private Stack filterLoop(double[] y) {
        int best = -1;
        for (int i = -1; i < y.length - inputsCount; i++) {
            int outputCount = 1;
            List dataList = TrainingSetImportStock.importDataElementsFromArray(y, inputsCount, outputCount, stack, i);
            DataSet trainingSet = TrainingSetImportStock.importFromDataElementList(dataList);
            NeuralNetwork neuralNet = new MultiLayerPerceptron(TransferFunctionType.GAUSSIAN, inputsCount, inputsCount * 2 + 1, outputCount);
            double maxError = 0.005D;
            ((LMS) neuralNet.getLearningRule()).setMaxError(maxError);
            double learningRate = 0.05D;
            ((LMS) neuralNet.getLearningRule()).setLearningRate(learningRate);
            ((LMS) neuralNet.getLearningRule()).setMaxIterations(MaxIterations);

            MomentumBackpropagation learningRule = ((MomentumBackpropagation) neuralNet.getLearningRule());
            learningRule.setLearningRate(learningRate);
            learningRule.setMaxError(maxError);
            learningRule.setMomentum(0.1);
            learningRule.setMinErrorChange(0.0001);
            learningRule.setMinErrorChangeIterationsLimit(100);//100 by Zoran

            neuralNet.learn(trainingSet);

            double difs = 0.0D;
            double sum = 0.0D;
            List<DataElement> dataListFull = TrainingSetImportStock.importDataElementsFromArray(y, inputsCount, outputCount, null, -1);
            for (DataElement aDataListFull : dataListFull) {
                DataSet testSet = new DataSet(inputsCount);
                testSet.addRow(new DataSetRow(aDataListFull.getInput()));
                for (DataSetRow testElement : testSet.getRows()) {
                    neuralNet.setInput(testElement.getInput());
                    neuralNet.calculate();
                    double[] networkOutput = neuralNet.getOutput();
                    difs = difs + Math.abs((Double) networkOutput[0] - aDataListFull.getOutput().get(0));
                    sum = sum + (Double) aDataListFull.getOutput().get(0);
                }
            }

            double rfactor = ((difs / sum) * 100.0D);

            if (rfactor < minrfactor) {
                minrfactor = rfactor;
                best = i;
                DataSet testSet1 = TrainingSetImportStock.importFromArrayToPredict(y, inputsCount);
                for (DataSetRow testElement : testSet1.getRows()) {
                    neuralNet.setInput(testElement.getInput());
                    neuralNet.calculate();
                    double[] networkOutput = neuralNet.getOutput();
                    bestpredicted = networkOutput[0];
                    //int bestinputsCount = inputsCount;
                    //Stack bestStack = stack;
                }
            }
        }
        if (!stack.contains(best)) {
            stack.add(best);
        }
        return stack;
    }


}
