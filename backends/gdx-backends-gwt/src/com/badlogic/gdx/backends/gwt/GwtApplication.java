package com.badlogic.gdx.backends.gwt;

import gwt.g2d.client.util.FpsTimer;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class GwtApplication implements EntryPoint, Application {
	private ApplicationListener listener;
	private GwtApplicationConfiguration config;
	private GwtGraphics graphics;
	private Label log = null;
	private int logLevel = LOG_ERROR;
	private List<Runnable> runnables = new ArrayList<Runnable>();
	
	@Override
	public void onModuleLoad() {
		this.listener = getApplicationListener();
		this.config = getConfig();

		graphics = new GwtGraphics(config);
		Gdx.graphics = graphics;
		Gdx.gl20 = graphics.getGL20();
		Gdx.gl = graphics.getGLCommon();
		
		setupLoop();
	}
	
	private void setupLoop() {
		// tell listener about app creation
		listener.create();
		listener.resize(graphics.getWidth(), graphics.getHeight());
		
		// add resize handler
		graphics.surface.addHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				GwtApplication.this.listener.resize(event.getWidth(), event.getHeight());
			}
		}, ResizeEvent.getType());
		
		// setup rendering timer
		FpsTimer timer = new FpsTimer(config.fps) {
			@Override
			public void update() {
				for(int i = 0; i < runnables.size(); i++) {
					runnables.get(i).run();
				}
				runnables.clear();
				listener.render();
			}
		};
		timer.start();
	}

	public abstract GwtApplicationConfiguration getConfig();
	public abstract ApplicationListener getApplicationListener();

	@Override
	public Graphics getGraphics() {
		return graphics;
	}

	@Override
	public Audio getAudio() {
		// FIXME
		throw new GdxRuntimeException("not implemented");
	}

	@Override
	public Input getInput() {
		// FIXME
		throw new GdxRuntimeException("not implemented");
	}

	@Override
	public Files getFiles() {
		// FIXME
		throw new GdxRuntimeException("not implemented");
	}

	private void checkLogLabel() {
		if(log == null) {
			log = new Label();
			RootPanel.get().add(log);
		}
	}
	
	@Override
	public void log(String tag, String message) {
		if(logLevel >= LOG_INFO) {
			checkLogLabel();
			log.setText(log.getText() + "\n" + tag + ": " + message);
		}
	}

	@Override
	public void log(String tag, String message, Exception exception) {
		if(logLevel >= LOG_INFO) {
			checkLogLabel();
			log.setText(log.getText() + "\n" + tag + ": " + message + "\n" + exception.getMessage());
		}	
	}

	@Override
	public void error(String tag, String message) {
		if(logLevel >= LOG_ERROR) {
			checkLogLabel();
			log.setText(log.getText() + "\n" + tag + ": " + message);
		}
	}

	@Override
	public void error(String tag, String message, Exception exception) {
		if(logLevel >= LOG_ERROR) {
			checkLogLabel();
			log.setText(log.getText() + "\n" + tag + ": " + message + "\n" + exception.getMessage());
		}		
	}

	@Override
	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	@Override
	public ApplicationType getType() {
		return ApplicationType.WebGL;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public long getJavaHeap() {
		return 0;
	}

	@Override
	public long getNativeHeap() {
		return 0;
	}

	@Override
	public Preferences getPreferences(String name) {
		// FIXME
		throw new GdxRuntimeException("not implemented");
	}

	@Override
	public void postRunnable(Runnable runnable) {
		runnables.add(runnable);
	}

	@Override
	public void exit() {		
	}
}
