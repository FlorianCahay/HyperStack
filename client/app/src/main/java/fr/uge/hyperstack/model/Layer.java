package fr.uge.hyperstack.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;

import fr.uge.hyperstack.model.drawing.Point;
import fr.uge.hyperstack.model.drawing.Rectangle;
import fr.uge.hyperstack.model.drawing.Point;
import fr.uge.hyperstack.model.drawing.Rectangle;
import fr.uge.hyperstack.model.media.Image;
import fr.uge.hyperstack.model.media.Video;
import fr.uge.hyperstack.view.EditorView;
import fr.uge.hyperstack.view.SlideView;
import fr.uge.hyperstack.view.listener.EditorViewListener;

/**
 * Classe représentant un calque qui contient un ou plusieurs éléments.
 * <p>
 * Un calque peut être partagés par plusieurs diapositives.
 *
 * @see Slide
 * @see PaintElement
 */
public class Layer implements Serializable  {
    /**
     * Ensemble des éléments présents sur ce calque.
     */
    private List<Element> elements;

    private final Context context;

    /**
     * La view sur laquelle le calque dessinera ses éléments.
     */
    private SlideView currentView;
    private ConstraintLayout layout;


    public Layer(Context context, ConstraintLayout layout) {
        this.elements = new ArrayList<>();
        this.currentView = new SlideView(context, layout);
        this.layout = layout;
        this.context = context;
    }


    /**
     * Ajoute un élément à afficher au calque.
     *
     * @param element l'élément à afficher.
     */
    public void addElement(@NonNull Element element) {
        elements.add(element);
    }

    /**
     * Enlève les éléments affichés via à ce calque.
     */
    public void clear() {
        layout.removeView(currentView);
    }

    public void erase() {
        layout.removeView(currentView);
        currentView.clear();
        elements.clear();
    }

    /**
     * Dessine tous les éléments de ce calque à l'écran.
     *
     * @see Element
     */
    public void draw() {
//        try (InputStream is = context.getAssets().open("data/cheval.png")) {
//            Bitmap b = BitmapFactory.decodeStream(is);
//            elements.add(new Image(b));
//            elements.add(new Rectangle(new Point(0f,0f),new Point(500,500), Color.RED,20));
//        } catch (IOException e) {
//            System.err.println("ERROR");
//        }
        clear();
        for (Element element : elements) {
            Log.d("draw", "draw");
            element.accept(currentView);
        }
        currentView.build();
        layout.addView(currentView);
    }

    public void setDrawableElements() {
        for (Element element : elements) {
            if (element instanceof PaintElement) {
                ((PaintElement) element).setPathOfStroke();
            }
        }
    }

    public EditorView getEditorView(){
        return currentView.getEditorView();
    }

}
