package Model;

import java.util.List;
import java.util.Random;

public class ModelHelper {
    private static final Random random = new Random();

    static <T> void randomize(T[] array){
        int size = array.length;
        int randomIndex;
        for (int i = 0; i < size; i++){
            randomIndex = random.nextInt(size);
            swap(array, i, randomIndex);
        }
    }
    private static <T> void swap(T[] array, int oldIndex, int newIndex){
        T tmp = array[oldIndex];
        array[oldIndex] = array[newIndex];
        array[newIndex] = tmp;
    }

    static <T> void randomize(List<T> list){
        int size = list.size();
        int randomIndex;
        for (int i = 0; i < size; i++){
            randomIndex = random.nextInt(size);
            swap(list, i, randomIndex);
        }
    }
    private static <T> void swap(List<T> list, int oldIndex, int newIndex){
        T tmp = list.get(oldIndex);
        list.set(oldIndex, list.get(newIndex));
        list.set(newIndex, tmp);
    }

    static ICell randomCell(List<ICell> cells){
        int index = random.nextInt(cells.size());
        return cells.get(index);
    }
}
