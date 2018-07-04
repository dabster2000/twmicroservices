/***
 * Neuroph  http://neuroph.sourceforge.net
 * Copyright by Neuroph Project (C) 2008
 *
 * This file is part of Neuroph framework.
 *
 * Neuroph is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Neuroph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Neuroph. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.trustworks.invoicewebui.ai.riskminimizer;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.TrainingSetImport;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * The part of simple stock market components, easy to use
 * the stock market interface for neural network.
 *
 * @author Valentin Steinhauer <valentin.steinhauer@t-online.de>
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class TrainingSetImportStock extends TrainingSetImport {

    public static DataSet importFromArray(double[] values, int inputsCount, int outputsCount) {
        DataSet trainingSet = new DataSet(4,1);
        for (int i = 0; i < values.length - inputsCount; i++) {
            ArrayList<Double> inputs = new ArrayList<>();
            for (int j = i; j < i + inputsCount; j++) {
                inputs.add(values[j]);
            }
            ArrayList<Double> outputs = new ArrayList<>();
            if (outputsCount > 0 && i + inputsCount + outputsCount <= values.length) {
                for (int j = i + inputsCount; j < i + inputsCount + outputsCount; j++) {
                    outputs.add(values[j]);
                }
                trainingSet.addRow(new DataSetRow(inputs, outputs));
            }
        }
        return trainingSet;
    }

    public static DataSet importFromArrayToPredict(double[] values, int inputsCount) {
        DataSet trainingSet = new DataSet(inputsCount);
        ArrayList<Double> inputs = new ArrayList<>();
        for (int j = values.length - inputsCount; j < values.length; j++) {
            inputs.add(values[j]);
        }
        trainingSet.addRow(new DataSetRow(inputs));
        return trainingSet;
    }

    public static DataSet importFromDataElementList(List<DataElement> dataList) {
        int inputSize = 0;
        int outputSize = 0;
        if(dataList.size()>0) {
            inputSize = dataList.get(0).getInput().size();
            outputSize = dataList.get(0).getOutput().size();
        }
        DataSet trainingSet = new DataSet(inputSize,outputSize);
        for (DataElement dataElement : dataList) {
            ArrayList inputs = dataElement.getInput();
            ArrayList outputs = dataElement.getOutput();
            if (outputs == null) {
                trainingSet.addRow(new DataSetRow(inputs));
            } else {
                trainingSet.addRow(new DataSetRow(inputs, outputs));
            }
        }
        return trainingSet;
    }

    public static double[] simpleNormalizing(double[] y, double norm) {
        for (int i = 0; i < y.length; i++) {
            y[i] = y[i] / norm;
        }
        return y;
    }


    /*
     * values - time series array
     * inputCount - window
     * blocked - number of window to block, -1 means not blocked
     * */
    public static List<DataElement> importDataElementsFromArray(double[] values, int inputsCount, int outputsCount, Stack stack, int next) {
        List<DataElement> list = new ArrayList();
        for (int i = 0; i < values.length - inputsCount; i++) {
            if (stack != null) {
                if (stack.contains(i)) {
                    continue;
                }
                if (stack.contains(next)) {
                    continue;
                }
            }
            if (i == next) {
                continue;
            }
            DataElement de = new DataElement();
            ArrayList<Double> inputs = new ArrayList<>();
            for (int j = i; j < i + inputsCount; j++) {
                inputs.add(values[j]);
            }
            ArrayList<Double> outputs = new ArrayList<>();
            if (outputsCount > 0 && i + inputsCount + outputsCount <= values.length) {
                for (int j = i + inputsCount; j < i + inputsCount + outputsCount; j++) {
                    outputs.add(values[j]);
                }
            }
            de.setInput(inputs);
            de.setOutput(outputs);
            list.add(de);
        }
        return list;
    }
}
