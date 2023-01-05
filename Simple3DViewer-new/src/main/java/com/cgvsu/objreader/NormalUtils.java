package com.cgvsu.objreader;

import java.util.*;

import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.model.*;

public class NormalUtils {

    public static List<Vector3f> calculateNormals(Model model) {
        ArrayList<float[]> normal = NormalUtils.calculateNormalsInMassiv(model);
        List<Vector3f> answer  = new ArrayList<Vector3f>();

        for (float[] current: normal) {
            //Vector3f vector = new Vector3f(current[0],current[1],current[2]);
            Vector3f vector = new Vector3f(current);
            answer.add(vector);
        }
        return answer;
    }

    public static ArrayList<float[]> calculateNormalsInMassiv(Model model) {
        ArrayList<float[]> vectors = new ArrayList<>();
        for (Vector3f wordsInLine : model.getVertices()) {
            float[] vector = new float[3];
            vector[0] = (wordsInLine.get(0));
            vector[1] = (wordsInLine.get(1));
            vector[2] = (wordsInLine.get(2));
            vectors.add(vector);
        }
        ArrayList<List<Integer>> polygons = new ArrayList<>();
        for (Polygon wordsInLine : model.getPolygons()) {
            polygons.add(wordsInLine.getVertexIndices());
        }

        HashMap<Integer, ArrayList<float[]>> map = new HashMap<>();

        for (List<Integer> wordsInLine : polygons) {
            Integer[] sample = new Integer[wordsInLine.size()];
            float[] coordinate;
            float[][] vertex = new float[sample.length][3]; //массив вершин текстуры

            for (int i = 0; i < sample.length; i++) { //для каждой ссылки вершины
                coordinate = vectors.get(sample[i]);

                if (coordinate.length != 0) {  //если мы что-то нашли
                    for (int j = 0; j < coordinate.length; j++) {
                        vertex[i][j] = coordinate[j];    // заполняем массив вершин
                    }
                }
            }

            float[] fNF = findNormalFace(vertex);
            HashSet<Integer> faces = findAllFaces(polygons);
            findNormalVertex(sample, fNF, faces, map);
        }
       return normal(map);
    }

    protected static float[] findNormalFace(float arr[][]) {
        float[][] vector = new float[arr.length - 1][3];
        Vector3f normal = new Vector3f();
        Vector3f norm = new Vector3f();
        float[] vector1 = new float[3];
        float[] vector2 = new float[3];
        float[] vectorSize = new float[3];
        //вычисление векторов из точек
        for (int i = 1; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                float x = arr[0][j] - arr[i][j];
                float y = arr[0][j];
                float z = arr[i][j];
                vector[i - 1][j] = x;
            }
        }
        for (int i = 0; i < vector[0].length; i++) {
            vector1[i] = vector[0][i];
            vector2[i] = vector[1][i];
        }
        normal.crossProduct(new Vector3f(vector1), new Vector3f(vector2));

        int size = 2;
        while (size != vector.length) {
            for (int i = 0; i < vector[0].length; i++) {
                vectorSize[i] = vector[size][i];
            }
            size++;
            norm.crossProduct(normal, new Vector3f(vectorSize));
        }

        return norm.getVector();
    }

    private static HashSet<Integer> findAllFaces(ArrayList<List<Integer>> polygons) {
        HashSet<Integer> face = new HashSet<Integer>();
        for (List<Integer> wordsInLine : polygons) {
            Integer[] sample = wordsInLine.toArray(new Integer[wordsInLine.size()]);
            for (int i = 0; i < sample.length; i++) {
                face.add(sample[i]);
            }
        }
        return face;
    }

    private static void findNormalVertex(Integer[] sample, float[] normalFace,
                                        HashSet<Integer> faces, HashMap<Integer, ArrayList<float[]>> map) {
        for (int i = 0; i < sample.length; i++) {
            if (faces.contains(sample[i])) {
                if (!map.containsKey(sample[i])) {
                    ArrayList<float[]> normalF = new ArrayList<>();
                    normalF.add(normalFace);
                    map.put(sample[i], normalF);
                } else {
                    ArrayList normal = map.get(sample[i]); //получаем список
                    normal.add(normalFace);
                }
            }
        }
    }

    protected static ArrayList<float[]> normal(HashMap<Integer, ArrayList<float[]>> map) {
        ArrayList<float[]> answer = new ArrayList<float[]>();
        for (Map.Entry entry : map.entrySet()) {
            float x = 0;
            float y = 0;
            float z = 0;
            ArrayList<float[]> current = (ArrayList<float[]>) entry.getValue();
            for (int i = 0; i < current.size(); i++) {
                float[] massiv = current.get(i);
                x += massiv[0];
                y += massiv[1];
                z += massiv[2];
            }

            x = x / current.size();
            y = y / current.size();
            z = z / current.size();
            float[] result = {x, y, z};
            current.clear();
            current.add(result);
            answer.add(result);
        }
        return answer;
    }

    public static void print(Model model) {
        List<Vector3f> normal = NormalUtils.calculateNormals(model);
        for (Vector3f current: normal) {
            System.out.print("vn " + current.get(0) + " " + current.get(1) + " " + current.get(3));
            System.out.println();
        }
    }
}
