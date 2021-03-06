package misc;

import java.util.List;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import game.Level;
import graphics.Camera;
import graphics.Model;
import graphics.Renderer;

public class CameraBoxSelection
{
	private final Vector3f max;
	private final Vector3f min;
	private final Vector2f nearFar;
	private Vector3f dir;
	
	public void selectModel(List<Model> models, Camera camera)
	{
		dir = Renderer.viewMatrix.positiveZ(dir).negate();
		selectModel(models, camera.getPosition(), dir);
	}
	
	protected void selectModel(List<Model> models, Vector3f center, Vector3f dir)
	{
		Model selectedModel = null;
		float closestDistance = Float.POSITIVE_INFINITY;
		
		for(Model model: models)
		{
			model.setSelected(false);
			min.set(model.getPosition());
			max.set(model.getPosition());
			//
			min.add(-model.getScale(), -model.getScale(), -model.getScale());
			max.add(model.getScale(), model.getScale(), model.getScale());
			if(Intersectionf.intersectRayAab(center, dir, min, max, nearFar) 
					&& nearFar.x < closestDistance)
			{
				closestDistance = nearFar.x;
				selectedModel = model;
			}
		}
		
		if(selectedModel != null)
		{
			selectedModel.setSelected(true);
		}
		else
		{
			if(Level.selectedTile != null)
				Level.selectedTile.getModel().setSelected(true);
		}
	}
	
	public CameraBoxSelection()
	{
		max = new Vector3f();
		min = new Vector3f();
		nearFar = new Vector2f();
		dir = new Vector3f();
	}
}
