package com.cgvsu.objwriter;


import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.model.TransformedModel;
import com.cgvsu.utils.FileManager;
import com.cgvsu.math.Math.Vector.Vector2f;
import com.cgvsu.math.Math.Vector.Vector3f;

import java.io.IOException;
import java.util.ArrayList;

public class ObjWriter {

    public static void write(final String name, final Model model) throws IOException {
        final ArrayList<String> vertices = verticesToString(model.vertices);
        final ArrayList<String> textureVertices = textureVerticesToString(model.textureVertices);
        final ArrayList<String> normals = normalsToString(model.normals);
        final ArrayList<String> poly = polygonsToString(model.getPolygons());

        FileManager.createFileWithText(name, vertices, textureVertices, normals, poly);
    }

    public static void writePrevModel(final String name, final TransformedModel model) throws IOException {
        final ArrayList<String> vertices = verticesToString(model.getPrevModel().vertices);
        final ArrayList<String> textureVertices = textureVerticesToString(model.getPrevModel().textureVertices);
        final ArrayList<String> normals = normalsToString(model.getPrevModel().normals);
        final ArrayList<String> poly = polygonsToString(model.getPrevModel().getPolygons());

        FileManager.createFileWithText(name, vertices, textureVertices, normals, poly);
    }

    public static void writeTransformedModel(final String name, final TransformedModel model) throws IOException {
        final ArrayList<String> vertices = verticesToString(model.getTransformedModel().vertices);
        final ArrayList<String> textureVertices = textureVerticesToString(model.getTransformedModel().textureVertices);
        final ArrayList<String> normals = normalsToString(model.getTransformedModel().normals);
        final ArrayList<String> poly = polygonsToString(model.getTransformedModel().getPolygons());

        FileManager.createFileWithText(name, vertices, textureVertices, normals, poly);
    }

    public static ArrayList<String> verticesToString(final ArrayList<Vector3f> array) {
        ArrayList<String> l = new ArrayList<String>();

        for (int vertexInd = 0; vertexInd < array.size(); vertexInd++) {
            l.add("v " + array.get(vertexInd).get(0) + " " + array.get(vertexInd).get(1) + " " + array.get(vertexInd).get(2));
        }

        return l;
    }

    public static ArrayList<String> textureVerticesToString(final ArrayList<Vector2f> array) {
        ArrayList<String> l = new ArrayList<String>();

        for (int textureVertices = 0; textureVertices < array.size(); textureVertices++) {
            l.add("vt " + array.get(textureVertices).get(0) + " " + array.get(textureVertices).get(1));
        }

        return l;
    }

    public static ArrayList<String> normalsToString(final ArrayList<Vector3f> array) {
        ArrayList<String> l = new ArrayList<String>();

        for (Vector3f vector3f : array) {
            l.add("vn " + vector3f.get(0) + " " + vector3f.get(1) + " " + vector3f.get(2));
        }

        return l;
    }

    public static ArrayList<String> polygonsToString(final ArrayList<Polygon> polygons) {
        ArrayList<String> l = new ArrayList<String>();
        String s;
        Polygon polygon = new Polygon();
        ArrayList<Integer> vertex = new ArrayList<Integer>();
        ArrayList<Integer> textureVertex = new ArrayList<Integer>();
        ArrayList<Integer> normal = new ArrayList<Integer>();

        for (int poly = 0; poly < polygons.size(); poly++) {
            s = "f";
            polygon = polygons.get(poly);
            vertex = polygon.getVertexIndices();
            textureVertex = polygon.getTextureVertexIndices();
            normal = polygon.getNormalIndices();

            for (int v = 0; v < vertex.size(); v++) {
                s += " ";
                s += vertex.get(v) + 1;

                if (textureVertex.size() != 0) {
                    s += "/";
                    s += textureVertex.get(v) + 1;
                }

                if (normal.size() != 0) {
                    s += "/";
                    s += normal.get(v) + 1;
                }
            }

            l.add(s);
        }

        return l;
    }

}

