package de.rub.SVRVKVE.input;

import java.util.ArrayList;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class InputHandler implements InputProcessor {
    private ArrayList<InputListener> inputListeners= new ArrayList<InputListener>();  
    InputListener listener = new InputListener();
    
    

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false; // Return true to say we handled the touch.
    }

    @Override
    public boolean keyDown(int keycode) {
    	for(InputListener listener: inputListeners){
    		listener.keyDown(null, keycode);
    	}
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
    	for(InputListener listener: inputListeners){
    		listener.keyUp(null, keycode);
    	}
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    
    public void addInputListener(InputListener listener){
    	inputListeners.add(listener);
    }
    
    public void removeInputListener(InputListener listener){
    	inputListeners.remove(listener);
    }

}
