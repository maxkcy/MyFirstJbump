package com.max.myfirstjbump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
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
	public Array<Item> itemArray;
	ShapeRenderer shapeDrawer;
	public Vector3 touch;
	Item<Entity> playerItem;
	float angle;
	float newX;
	float newY;
	boolean touching;
	int acceleration = 5;
	int velocity = 0;

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


		Entity player = new Entity(152, 40, 5, 5);
		playerItem = new Item<>(player);
		world.add(playerItem, player.position.x, player.position.y, player.width, player.height);
		itemArray.add(playerItem);


	}
	CollisionFilter myFirstCollision = new CollisionFilter() {
		@Override
		public Response filter(Item item, Item other) {
			if(other.userData instanceof Entity){return Response.touch;}
			return null;
		}

	};
	 public void movement() {
		if(Gdx.input.isTouched()) {
			touch = viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			angle = MathUtils.atan2(touch.y - playerItem.userData.position.y, touch.x - playerItem.userData.position.x)
					* MathUtils.radiansToDegrees;
			angle = (((angle % 360) + 360) % 360);
			if(touch.x - playerItem.userData.position.x > 5 || touch.x - playerItem.userData.position.x < -5){
				newX = playerItem.userData.position.x + (MathUtils.cosDeg(angle) * 5);
			} else { newX = touch.x;}
			if(touch.y - playerItem.userData.position.y > 5 || touch.y - playerItem.userData.position.y < -5){
				newY = playerItem.userData.position.y + (MathUtils.sinDeg(angle) * 5);
			} else {newY = touch.y;}
			world.move(playerItem, newX, newY, myFirstCollision);
			//last spot is here, this is the new spot so body can be told to move in direction
			//playerItem.userData.position.x = newX;
			//playerItem.userData.position.y = newY;
			//fixed under
			playerItem.userData.position.x = world.getRect(playerItem).x;
			playerItem.userData.position.y = world.getRect(playerItem).y;
			//System.out.println(playerItem.userData.position);

		}
	 }

	 public void jump() {
		 newX = playerItem.userData.position.x;
		 newY = playerItem.userData.position.y;
	 	Response.Result move = world.move(playerItem, newX, newY - 5, myFirstCollision);
		 for (int i = 0; i < move.projectedCollisions.size(); i++) {
			 Collision collision = move.projectedCollisions.get(i);
			 System.out.println(collision);
			 Object userData = collision.other.userData;
			 //System.out.println(userData);
			 if (userData instanceof Entity) {
				 touching = true;
				 if (Gdx.input.isTouched()) {
					touch = viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
					 /*angle = MathUtils.atan2(touch.y - playerItem.userData.position.y, touch.x - playerItem.userData.position.x)
							 * MathUtils.radiansToDegrees;
					 angle = (((angle % 360) + 360) % 360);*/
					 velocity = 15;
					 touching = false;
					 newX = touch.x;
				 }
			 }
		 }
		 newY += velocity;
		 world.move(playerItem, newX, newY, myFirstCollision);
		 playerItem.userData.position.x = world.getRect(playerItem).x;
		 playerItem.userData.position.y = world.getRect(playerItem).y;
		 velocity -= acceleration;
		 System.out.println(touching);
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
		movement();
		//jump();
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
