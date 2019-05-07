import lombok.Data;

import java.util.List;

@Data
public class Boundary {

    private String type;

    private List<Feature> features;

    @Data
    public static class Feature {
        private String type;

        private Geometry geometry;

        private Property properties;
    }

    @Data
    public static class Property {
        private int id;

        private String name;

        List<Double> cp;

        private int childNum;
    }

    @Data
    public static class Geometry {
        private String type;

        private List<List<List<Double>>> coordinates;
    }
}
