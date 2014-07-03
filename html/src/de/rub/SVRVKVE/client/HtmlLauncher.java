package de.rub.SVRVKVE.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import de.rub.SVRVKVE.simulation.HerdSimulation;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(HerdSimulation.WINDOW_X, HerdSimulation.WINDOW_Y);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new HerdSimulation();
        }
}