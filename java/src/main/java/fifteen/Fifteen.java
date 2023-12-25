package fifteen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Fifteen {
    public static void main(String[] args) throws IOException {
        List<String> strings = List.of(Files.readString(Path.of(args[0])).strip().split(","));
        int firstSolution = strings.stream()
                .mapToInt(HASHMAP::hashString)
                .sum();
        System.out.println(firstSolution);
        System.out.println(HASHMAP.hashString("rn"));

        HASHMAP map = new HASHMAP();
        for (String string : strings) {
            if (string.endsWith("-")) {
                String label = string.substring(0, string.length() - 1);
                map.delete(label);
            } else {
                String[] split = string.split("=");
                map.add(split[0], Byte.valueOf(split[1]));
            }
            System.out.println(map.getBuckets().subList(0,4));
        }

        int sum = 0;

        List<List<LabeledFocalLength>> buckets = map.getBuckets();
        System.out.println(buckets);
        for (int i = 0; i < buckets.size(); i++) {
            List<LabeledFocalLength> bucket = buckets.get(i);
            for (int j = 0; j < bucket.size(); j++) {
                int newVal = (i + 1) * (j + 1) * bucket.get(j).focalLength();
                System.out.println(newVal);
                sum += newVal;
            }
        }

        System.out.println(sum);
    }

}

record LabeledFocalLength(String label, byte focalLength) {


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LabeledFocalLength other = (LabeledFocalLength) obj;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return label + " " + focalLength;
    }


}

class HASHMAP {
    private final List<List<LabeledFocalLength>> buckets = new ArrayList<>(256);

    public HASHMAP() {
        for (int i = 0; i < 256; i++)
            buckets.add(new ArrayList<>());

    }

    void delete(String label) {
        int hash = hashString(label);
        buckets.get(hash)
                .removeIf(i -> i.label().equals(label));
    }

    void add(String label, byte value) {
        int hash = hashString(label);
        List<LabeledFocalLength> bucket = buckets.get(hash);
        LabeledFocalLength newLabel = new LabeledFocalLength(label, value);
        int indexOf = bucket.indexOf(newLabel);
        System.out.println(indexOf);
        if (indexOf == -1)
            bucket.add(newLabel);
        else
            bucket.set(indexOf, newLabel);

    }

    public static int hashString(String string) {
        return string.chars()
                .reduce(0, (prev, next) -> ((prev + next) * 17) % 256);
    }

    public List<List<LabeledFocalLength>> getBuckets() {
        return buckets;
    }
}
