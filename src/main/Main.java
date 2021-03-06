package main;

import java.io.File;
import java.util.Random;
import misc.Timer;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import game.Level;
import graphics.Camera;
import graphics.HUD;
import graphics.Renderer;
import input.Keyboard;
import input.Mouse;
import misc.Assets;
import misc.Defines;
import game.Nation;

public class Main
{
	public static boolean running;
	public static Random randomGenerator;
	public static Window window;
	public static Camera camera;
	public static long vg;
	private Level level;
	private Timer timer;
	private Renderer renderer;
	private Keyboard keyboard;
	private Mouse mouse;
	private HUD hud;
	private boolean debug = true;
	private double lastFps = 0;
	public static long frame;
	
	private void cleanup()
	{
		NanoVGGL3.nvgDelete(vg);
		renderer.cleanup();
		Assets.cleanup();
		window.cleanup();
	}
	
	private void sync()
	{
		float loopSlot = 1f / (float)Defines.FRAMES_PER_SECOND;
		double endTime = timer.getLastLoopTime() + loopSlot;
		
		while(timer.getTime() < endTime)
		{
			try
			{
				Thread.sleep(1);
			}
			catch(InterruptedException e)
			{
				System.err.println(e);
			}
		}
	}
	
	private void init()
	{
		if(!GLFW.glfwInit())
			System.out.println("Could not init GLFW!");
		Defines.init();
		window = new Window(Defines.widthResolution, Defines.heightResolution, Defines.title);
		GL.createCapabilities();
		//GL11.glViewport(0, 0, window.getWindowWidth(), window.getWindowHeight());
		vg = NanoVGGL2.nvgCreate(0);
		setupFonts();
		
		Assets.init();
		randomGenerator = new Random();
		level = new Level(new Nation("Prussia", "Prussian", "Protestant", 
				new Vector3f(0.0f, 0.192f, 0.325f)), 5, 5);
		renderer = new Renderer();
		renderer.init();
		camera = new Camera();
		running = true;
		keyboard = new Keyboard(window.getWindowID());
		mouse = new Mouse(window);
		hud = new HUD();
		frame = 0;
		
		GL11.glClearColor(1.0f, 0.5f, 0.2f, 0.0f);
		openGLFlags();
		
		
		timer = new Timer();
		timer.init();
	}
	
	private void update()
	{
		Mouse.update();
		//Mouse.update();
		
		window.update();
		
		camera.update();
		
		hud.update();
		
		if(level != null)
			level.update();
	}
	
	private void openGLFlags()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void setupFonts()
	{
		String path = new File("D:/Downloads/Consolas.ttf").getAbsolutePath();
		NanoVG.nvgCreateFont(Main.vg, "Consolas", path);
	}
	
	private void renderGui()
	{
		NanoVG.nvgSave(vg);
		float pixelRatio = window.getWindowWidth() / window.getWindowHeight();
		NanoVG.nvgBeginFrame(vg, window.getWindowWidth(), 
				window.getWindowHeight(), pixelRatio);
		//render start
		hud.render();
		//render end
		NanoVG.nvgEndFrame(vg);
		//NanoVG.nvgSave(Main.vg);
		//NanoVG.nvgRestore(vg);
		NanoVG.nvgRestore(Main.vg);
	}
	
	private void render()
	{
		openGLFlags();
		renderer.clear();
		window.clear();
		
		hud.render();
		if(level != null)
			level.render();
		
		renderer.render();
		renderGui();
		window.render();
	}
	
	private void run()
	{
		float elapsedTime;
		float accumulator = 0.0f;
		float interval = (float)(1.0f / Defines.FRAMES_PER_SECOND);
		int frames = 0;
		int updates = 0;
		
		while(running)
		{
			if(GLFW.glfwWindowShouldClose(window.getWindowID()))
				running = false;
			
			elapsedTime = timer.getElapsedTime();
			accumulator += elapsedTime;
			
			keyboard.update();
			mouse.input();
			
			while(accumulator >= interval)
			{
				
				update();
				accumulator -= interval;
				updates++;
				frame++;
			}
			
			render();
			frames++;
			
			if(debug && timer.getLastLoopTime() - lastFps > 1)
			{
				window.setTitle(Defines.title + " " + "frames: " + frames + ";updates: " + updates);
				lastFps = timer.getLastLoopTime();
				updates = 0;
				frames = 0;
			}
			
			sync();
		}
		cleanup();
	}
	
	public void start()
	{
		init();
		run();
	}
	
	public static void main(String args[])
	{
		Main game = new Main();
		game.start();
	}
}
