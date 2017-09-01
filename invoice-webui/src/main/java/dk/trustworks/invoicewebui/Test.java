package dk.trustworks.invoicewebui;

import java.util.*;

/**
 * Created by hans on 31/08/2017.
 */
public class Test {

    public static void main(String[] args) {

        List<Box> boxes = new ArrayList<>();
        boxes.add(new Box(3, 2));
        boxes.add(new Box(2, 1));
        boxes.add(new Box(4, 2));
        boxes.add(new Box(3, 1));
        boxes.add(new Box(1, 1));
        boxes.add(new Box(1, 3));
        boxes.add(new Box(5, 1));

        boxes.sort(Comparator.comparing(Box::getPriority)
                .thenComparing(Box::getWidth));

        int[] rowSize = new int[10];
        Map<Box, Integer> boxIntegerMap = new HashMap<>();
        for (Box box : boxes) {
            for (int i = 0; i < rowSize.length; i++) {
                System.out.println("--- --- ---");
                System.out.println("i = " + i);
                System.out.println("rowSize = " + rowSize[i]);
                System.out.println("box = " + box);
                if(rowSize[i] + box.getWidth() <= 4) {
                    boxIntegerMap.put(box, i);
                    rowSize[i] += box.getWidth();
                    System.out.println("--- put ---");
                    System.out.println("--- --- ---");
                    break;
                }
            }
        }

        for (Box box : boxIntegerMap.keySet()) {
            Integer integer = boxIntegerMap.get(box);
            System.out.println(integer + ": "+box);
        }
    }

    public static class Box {

        public int priority;
        public int width;

        public Box(int priority, int width) {
            this.priority = priority;
            this.width = width;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Box{");
            sb.append("priority=").append(priority);
            sb.append(", width=").append(width);
            sb.append('}');
            return sb.toString();
        }
    }

}
