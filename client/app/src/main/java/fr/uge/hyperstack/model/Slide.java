package fr.uge.hyperstack.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une diapositive (ou aussi planche) d'une présentation.
 * <p>Elle peut contenir différents éléments :</p>
 * <ul>
 *     <li>des croquis</li>
 *     <li>du texte</li>
 *     <li>des images et vidéos</li>
 *     <li>des sons</li>
 *     <li>des géocalisations</li>
 *     <li>des entrées utilisateur (texte, checkbox, radio)</li>
 * </ul>
 * <p>
 * Une diapositive est la superposition de différents calques.
 * Les éléments sont placés sur ces derniers et non la diapositive directement.
 *
 * @see Layer
 * @see Stack
 */
public class Slide implements Serializable {
    /**
     * Ensemble des calques de cette diapositive.
     */
    private List<Layer> layers;


    public Slide() {
        this.layers = new ArrayList<>();
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    public void drawLayers(Canvas canvas, Paint paint) {
        for (Layer layer : layers) {
            layer.drawElements(canvas, paint);
        }
    }

}
