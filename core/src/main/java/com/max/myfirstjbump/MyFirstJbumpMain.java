package com.max.myfirstjbump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.World;

import java.awt.image.ImagingOpException;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyFirstJbumpMain extends Game {
	private SpriteBatch batch;
	//private Texture image;

	public Camera camera;
	public Viewport viewport;
	public TmxMapLoader mapLoader;
	public TiledMap map;
	public OrthogonalTiledMapRenderer mapRenderer;
	public World<Entity> world;
	public Item<Entity> myFirstItem;
	public Array<Item> itemArray;
	ShapeRenderer shapeDrawer;

	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeDrawer = new ShapeRenderer();
		itemArray = new Array<>();
		//image = new Texture("badlogic.png");

		mapLoader = new TmxMapLoader();
		map = mapLoader.load("Map.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);


		camera = new OrthographicCamera();
		viewport = new FitViewport(384, 216, camera);

		camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);

		world = new World<Entity>(8);

		for (MapObject mapObject : map.getLayers().get(2).getObjects()){
			if(mapObject instanceof RectangleMapObject){
				Rectangle rects = ((RectangleMapObject) mapObject).getRectangle();
				Entity platForm = new Entity(rects.x, rects.y, rects.width, rects.height);
				Item item = new Item<Entity>(platForm);
				world.add(item, platForm.position.x, platForm.position.y,
						platForm.width, platForm.height);
				itemArray.add(item);

			}

		}



	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		viewport.apply();  // because camera can have many viewports
		mapRenderer.setView((OrthographicCamera) camera);
		mapRenderer.render();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();


		batch.end();
		shapeDrawer.setProjectionMatrix(camera.combined);
		shapeDrawer.setAutoShapeType(true);
		shapeDrawer.begin();
		for (Item item : itemArray) {
			if (item != null) {
				shapeDrawer.setColor(Color.RED);

				Rect rect = world.getRect(item);
				shapeDrawer.rect(rect.x, rect.y, rect.w, rect.h);

			}
		}
		shapeDrawer.end();
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
		camera.update();

		super.resize(width, height);
	}

	@Override
	public void dispose() {
		batch.dispose();
		map.dispose();
		//image.dispose();
	}
}