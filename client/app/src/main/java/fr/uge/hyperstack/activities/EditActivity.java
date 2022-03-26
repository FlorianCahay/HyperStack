package fr.uge.hyperstack.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.uge.hyperstack.R;
import fr.uge.hyperstack.fragment.ImportImageDialogFragment;
import fr.uge.hyperstack.fragment.SlideBottomBarDialogFragment;
import fr.uge.hyperstack.model.PaintElement;
import fr.uge.hyperstack.fragment.ImportSoundDialogFragment;
import fr.uge.hyperstack.model.Mode;
import fr.uge.hyperstack.model.drawing.Circle;
import fr.uge.hyperstack.model.drawing.Point;
import fr.uge.hyperstack.model.drawing.Rectangle;
import fr.uge.hyperstack.model.Stack;
import fr.uge.hyperstack.model.drawing.Triangle;
import fr.uge.hyperstack.model.media.Image;
import fr.uge.hyperstack.model.media.Sound;
import fr.uge.hyperstack.utils.Localisation;
import fr.uge.hyperstack.utils.Permission;

@SuppressLint("NonConstantResourceId")
public class EditActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    /**
     * Présentation sélectionnée par l'utilisateur et affichée à l'écran.
     */
    private Stack currentStack;
    /**
     * Numéro de la slide qui doit être affichée à l'écran.
     * <p>
     * Le numéro 0 correspond à la première slide.
     */
    private int currentSlideNumber = 0;
    /**
     * Dialog pour l'importation d'image/vidéo.
     */
    private ImportImageDialogFragment imageDialogFragment;
    /***
     * Dialog pour l'importation de son.
     */
    private ImportSoundDialogFragment soundDialogFragment;
    /**
     * Dialog affichant la liste des slides de la présentation.
     */
    private SlideBottomBarDialogFragment slideBottomBarDialogFragment;
    private final List<Sound> soundList = new ArrayList<>();
    private Localisation localisation;
    private static Mode currentMode = Mode.SELECTION;
    private final List<PaintElement> strokeStack = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent homeIntent = getIntent();

        currentStack = (Stack) homeIntent.getSerializableExtra("stack");
        currentStack.initSlideLayer(getApplicationContext(), findViewById(R.id.editLayout));

        setEditSetup();
        setUpEditMode();

        imageDialogFragment = new ImportImageDialogFragment();
        soundDialogFragment = new ImportSoundDialogFragment();
        slideBottomBarDialogFragment = new SlideBottomBarDialogFragment(currentStack.getSlides());

        updateSlideNumberLabel();

        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setNavigationOnClickListener(view -> {
            slideBottomBarDialogFragment.show(getSupportFragmentManager(), "slideBottomBar");
        });


        bottomAppBar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.previousSlide:
                    if (currentSlideNumber > 0) {
                        currentStack.clearSlide(currentSlideNumber);
                        currentSlideNumber--;
                        updateSlideNumberLabel();
                        currentStack.drawSlide(currentSlideNumber);
                    }
                    return true;
                case R.id.nextSlide:
                    if (currentStack.sizeOfStack() > 0 && currentSlideNumber < currentStack.sizeOfStack() - 1) {
                        currentStack.clearSlide(currentSlideNumber);
                        currentSlideNumber++;
                        updateSlideNumberLabel();
                        currentStack.drawSlide(currentSlideNumber);
                    }
                    return true;
                default:
                    return false;
            }
        });
        currentStack.setDrawableElements();

//        Button backButton = findViewById(R.id.backEditButton);
//        backButton.setOnClickListener(v -> {
//            Intent intent = new Intent();
//            editorView.getCurrentStack().addNewSlide();
//            intent.putExtra("newCurrentStack", editorView.getCurrentStack());
//            intent.putExtra("stackNum", currentStackNum);
//            setResult(RESULT_OK, intent);
//            finish();
//        });

        refresh();
    }

    /**
     * Met à jour le label indiquant quelle slide est affichée et combien il y a de slide en tout.
     */
    private void updateSlideNumberLabel() {
        TextView slideNumberTextView = findViewById(R.id.slideNumberBottomBarTextView);
        slideNumberTextView.setText(String.format(getResources().getString(R.string.slide_number_bottom_bar), currentSlideNumber + 1, currentStack.sizeOfStack()));
    }

    /**
     * Supprime tous les éléments affichés et les réaffiche.
     */
    public void refresh() {
        currentStack.clearSlide(currentSlideNumber);
        currentStack.drawSlide(currentSlideNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    private void showPopupMenu(int anchorId, int menu) {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(anchorId));
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(menu);
        popupMenu.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ICI pour les boutons dans menu_edit.xml
        switch (item.getItemId()) {
            case R.id.action_insert_mode:
                showPopupMenu(R.id.action_insert_mode, R.menu.menu_insertion_mode);
                return true;
            case R.id.action_draw_mode:
                showPopupMenu(R.id.action_draw_mode, R.menu.menu_drawing_mode);
                return true;
            case R.id.action_erase:
                clearSlide();
                return true;
            case R.id.logs:
                goToLogs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // ICI pour les boutons dans menu_drawing_mode.xml et menu_insertion_mode.xml
        switch (item.getItemId()) {
            case R.id.action_add_text:
                editSlideText();
                return true;
            case R.id.action_add_image:
                showImportImageVideoDialog();
                return true;
            case R.id.action_add_sound:
                showImportSoundDialog();
                return true;
            case R.id.action_add_location:
                if(localisation == null)
                    localisation = new Localisation(this);
                localisation.runWithPermission(Manifest.permission.ACCESS_FINE_LOCATION, "geolocalisation");
                return true;
            case R.id.action_add_user_input:
                // TODO : implement this function
                return true;
            case R.id.action_add_rectangle:
                setFigureSetup();
                currentMode = currentMode != Mode.RECTANGLE ? Mode.RECTANGLE : Mode.SELECTION;
                return true;
            case R.id.action_add_triangle:
                setFigureSetup();
                currentMode = currentMode != Mode.TRIANGLE ? Mode.TRIANGLE : Mode.SELECTION;
                return true;
            case R.id.action_add_circle:
                // TODO : implement this function
                return true;
            default:
                return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Permission.IMAGE_CAPTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Image img = new Image((Bitmap) extras.get("data"));
                    currentStack.addElementToSlide(img, currentSlideNumber);
                    imageDialogFragment.dismiss();
                }
                break;
            case Permission.VIDEO_CAPTURE_REQUEST_CODE:
                break;
            case Permission.SOUND_TAKEN_FROM_APP_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Sound sound = (Sound) data.getExtras().get("sound");
                    soundList.add(sound);
                    try {
                        sound.getSound(this);
                        Toast.makeText(this, sound.getName(), Toast.LENGTH_SHORT).show();
                        Log.e("Sound", sound.getName());
                    } catch (IOException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Sound", e.getMessage(), e);
                    }
                    soundDialogFragment.dismiss();
                }
                break;
            case Permission.SOUND_IMPORT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if ((data != null) && (data.getData() != null)){
                        Uri audioURI = data.getData();
                        Sound sound = new Sound("test");
                        soundList.add(sound);
                        // TODO faire un bouton play/pause
                        try {
                            sound.playSound(this, audioURI);
                            Toast.makeText(this, "Sound is Playing", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e("SoundError", e.getMessage(), e);
                            Toast.makeText(this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        soundDialogFragment.dismiss();
                    }
                }
        }

        refresh();
    }

    /**
     * Loses focus on TextView when clicking outside of the TextView
     * See https://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * Ajoute une nouvelle slide à la présentation courante.
     *
     * @param v
     */
    public void onClickAddSlide(View v) {
        currentStack.addNewSlide();
        updateSlideNumberLabel();
    }

    private void editSlideText() {
        TextView et = findViewById(R.id.slide_text);

        et.setVisibility(View.VISIBLE);
        et.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void clearSlide() {
//        Stack s = editorView.getCurrentStack();
//        s.resetSlide(editorView.currentSlide);
//        editorView.invalidate();
    }

    private void goToLogs() {
//        EditorView ev = findViewById(R.id.editorView2);
//        Intent intent = new Intent(this, LogsActivity.class);
//        intent.putExtra("logs", ev.getCurrentStack().getLogs());
//        startActivityForResult(intent, 1);
    }

    /**
     * Affiche le BottomSheetDialog proposant d'importer une photo/vidéo ou de prendre une photo/vidéo directement.
     */
    private void showImportImageVideoDialog() {
        imageDialogFragment.show(getSupportFragmentManager(), "importImage");
    }

    /**
     * Affiche le BottomSheetDialog proposant d'importer un son.
     */
    private void showImportSoundDialog() {
        soundDialogFragment.show(getSupportFragmentManager(), "importSound");
    }

    private void setSoundPlayer(Sound sound) {
        try {
            sound.getSound(this);
        } catch (IOException e) {
            Log.e("Sound", e.getMessage(), e);
        }
    }

    public void setEditSetup() {
//        editorView.setEditorViewListener(new EditorViewListener() {
//            @Override
//            public void onFingerTouch(float x, float y) {
//                Stroke stroke = new Stroke(Color.RED, 25);
//                stroke.moveTo(x,y);
//                editorView.getStrokeStack().add(stroke);
//                editorView.getCurrentStack().addLayerElementToSlide(stroke, editorView.currentSlide);
//            }
//
//            @Override
//            public void onFingerMove(float x, float y) {
//                PaintElement currentElem = editorView.getStrokeStack().peek();
//                currentElem.onFingerMoveAction(x,y);
//            }
//
//            @Override
//            public void onFingerRaise(float x, float y) { }
//        });
    }

    public static PaintElement initFigure(float x, float y){
        switch (currentMode){
            case RECTANGLE: return new Rectangle(new Point(x,y),new Point(x,y),Color.RED,10);
            case TRIANGLE: return new Triangle(Color.RED,10,new Point(x,y),new Point(x,y),new Point(x,y));
            case CIRCLE: return new Circle(Color.RED,new Point(x,y),10,new Point(x,y));
            default: return null;
        }
    }


    /**
     * Set the listener when a draw figure button is called
     */
    public void setFigureSetup() {
        View view = findViewById(R.id.editLayout);
        view.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                PaintElement element = initFigure(motionEvent.getX(), motionEvent.getY());
                                strokeStack.add(element);
                                currentStack.addElementToSlide(element, currentSlideNumber);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                PaintElement currentElem = strokeStack.get(strokeStack.size() - 1);
                                currentElem.onFingerMoveAction(motionEvent.getX(), motionEvent.getY());
                                break;
                            case MotionEvent.ACTION_UP:
                                currentMode = Mode.SELECTION;view.setOnTouchListener(null);
                                break;
                        }
                        return true;
                    }
                }
        );
    }

//    public void setFigureSetup() {
//        editorView.setCurrentStack((Stack) homeIntent.getSerializableExtra("stack"));
//        editorView.setEditorViewListener(new EditorViewListener() {
//            @Override
//            public void onFingerTouch(float x, float y) {
//                PaintElement element = initFigure(x, y, editorView);
//                editorView.getStrokeStack().add(element);
//                editorView.getCurrentStack().addLayerElementToSlide(element, editorView.currentSlide);
//            }
//
//            @Override
//            public void onFingerMove(float x, float y) {
//                PaintElement currentElem = editorView.getStrokeStack().peek();
//                currentElem.onFingerMoveAction(x,y);
//            }
//
//            @Override
//            public void onFingerRaise(float x, float y) {
//                editorView.setCurrentMode(Mode.SELECTION);
//            }
//        });
//    }

    private void setUpEditMode() {
//        editButton.setOnClickListener(
//            (view) -> {
//                setEditSetup();
//                if (!editorView.drawModeOn) {
//                    editorView.drawModeOn = true;
//                    editorView.setCurrentMode(Mode.DRAW);
//                    editButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
//                } else{
//                    editorView.drawModeOn = false;
//                    editorView.setCurrentMode(Mode.SELECTION);
//                    editButton.setImageResource(android.R.drawable.ic_menu_edit);
//                }
//            }
//        );
    }


}