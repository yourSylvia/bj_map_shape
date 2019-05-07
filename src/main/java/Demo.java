import com.alibaba.fastjson.JSON;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Demo {

    public static void main(String[] args) throws IOException {
        H3Core h3 = H3Core.newInstance();
        String boundary = "{\"type\": \"Feature\",\"properties\":{\"id\":\"11\",\"name\":\"北京\",\"cp\":[116.4551,"
                        + "40.2539],\"childNum\":19},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[117.4219,"
                        + "40.21],[117.334,40.1221],[117.2461,40.0781],[116.8066,39.9902],[116.8945,39.8145],"
                        + "[116.8945,39.6826],[116.8066,39.5947],[116.543,39.5947],[116.3672,39.4629],[116.1914,"
                        + "39.5947],[115.752,39.5068],[115.4883,39.6387],[115.4004,39.9463],[115.9277,40.2539],"
                        + "[115.752,40.5615],[116.1035,40.6055],[116.1914,40.7813],[116.4551,40.7813],[116.3672,"
                        + "40.9131],[116.6309,41.0449],[116.9824,40.6934],[117.4219,40.6494],[117.2461,40.5176],"
                        + "[117.4219,40.21]]]}}";
        Boundary.Feature b = JSON.parseObject(boundary, Boundary.Feature.class);
        List<List<Double>> geos = b.getGeometry().getCoordinates().get(0);
        List<GeoCoord> geoCoords = new ArrayList<>();
        int resolution = 4;

        geos.forEach(value -> {
            double lng = value.get(0);
            double lat = value.get(1);
            GeoCoord geoCoord = new GeoCoord(lat, lng);
            geoCoords.add(geoCoord);
        });

        // get center coordinates
        List<String> geoH3Addr = h3.polyfillAddress(geoCoords, null, resolution);
        System.out.println("size is : " + geoH3Addr.size());

        final BufferedWriter csvFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("bj_map" + resolution + ".csv"), StandardCharsets.UTF_8));

        for(int j=0; j<6; j++){
            csvFile.write("lng" + j +  "," + "lat" + j + ",");
        }
        csvFile.write("points,");
        csvFile.write("center");
        csvFile.newLine();

        geoH3Addr.forEach(addr -> {

            try {
                exportCSVfile(csvFile, h3.h3ToGeoBoundary(addr), h3.h3ToGeo(addr));
            }
            catch (Exception e){
                System.out.println("Exception");
            }
        });

        csvFile.close();
    }


    public static void exportCSVfile(BufferedWriter csvFile, List<GeoCoord> vertices, GeoCoord centroid) throws Exception {
        String delim = ",";

        try {
            StringBuilder buf = new StringBuilder();
            String s = "\"";
            for(int j=0; j < vertices.size(); j++){
                buf.append(vertices.get(j).lng);
                buf.append(delim);
                buf.append(vertices.get(j).lat);
                buf.append(delim);
                if(j < 5){
                    s = s + "[" + vertices.get(j).lng + "," + vertices.get(j).lat + "]" + ",";
                }
                else {
                    s = s + "[" + vertices.get(j).lng + "," + vertices.get(j).lat + "]";
                }
            }

            s = s + "\"";
            csvFile.write(buf.toString());
            csvFile.write(s + ",");
            csvFile.write("\"[" + centroid.lng + ","  + centroid.lat + "]\"");
            csvFile.newLine();
        }
        catch (IOException e) {
            // empty
        }
    }
}
