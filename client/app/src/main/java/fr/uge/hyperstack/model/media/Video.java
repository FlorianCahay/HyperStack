package fr.uge.hyperstack.model.media;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;

import fr.uge.hyperstack.model.ElementVisitor;
import fr.uge.hyperstack.model.MediaElement;

public class Video implements MediaElement {
    private final Uri location;

    public Video(@NonNull Uri location) {
        this.location = location;
    }

    @Override
    public float getPositionX() {
        return 0;
    }

    @Override
    public float getPositionY() {
        return 0;
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public float convertBiasHorizontal(float width) {
        return 0;
    }

    @Override
    public float convertBiasVertical(float height) {
        return 0;
    }

    @Override
    public void accept(ElementVisitor elementVisitor) {

    }

}
