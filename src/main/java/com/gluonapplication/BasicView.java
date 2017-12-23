package com.gluonapplication;

import java.net.URISyntaxException;
import java.util.Random;

import com.gluonhq.charm.down.Platform;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.util.Duration;

public class BasicView extends View {
	private double width, height, x1Initial, y1Initial, x2Initial, y2Initial;
	private ImageView ivCannon2, ivCannon1;
	private Rectangle rectEnclosingCannon2, rectEnclosingCannon1;
	private final double MASS_CONSTANT = 5, RADIUS = 10;
	private Pane pane;
	private GridPane gPane;
	private Boolean player1Turn = true;
	private Button btFire;
	private Label youWonLabel = new Label("");
	private Label lbPlayer, lb2Player;
	private NativeAudioService service;
	private MediaPlayer[] mp;
	private QuadCurve quad;

	public BasicView(String name) {
		super(name);

		// Check if platform is android , if so then instantiate the service
		if (Platform.isAndroid()) {
			try {
				service = (NativeAudioService) Class.forName("com.gluonapplication.AndroidNativeAudio").newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
				System.out.println("Error " + ex);
			}
		}

		// Get the dimensions of Screen , and assign random coordinates as
		// follows
		Rectangle2D rect2D = Screen.getPrimary().getVisualBounds();
		width = rect2D.getWidth();
		height = rect2D.getHeight();
		Random random = new Random();
		x1Initial = 25 + random.nextInt((int) ((width / 4) - 25));
		x2Initial = ((3*width)/4)+ random.nextInt((int) (width / 4)-25);
		y1Initial = 90 + random.nextInt((int) (height / 7));
		y2Initial = 90 + random.nextInt((int) (height / 7));;

		// Create the Pane in order to assign controls by coordinates
		pane = new Pane();
		pane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 255), null, null)));

		// Player 1's cannon
		Image cannon1Image = new Image("cannon.png");
		ivCannon1 = new ImageView();
		ivCannon1.setImage(cannon1Image);
		ivCannon1.setFitWidth(50);
		ivCannon1.setPreserveRatio(true);
		ivCannon1.setSmooth(true);
		ivCannon1.setCache(true);
		ivCannon1.setX(x1Initial - 25);
		ivCannon1.setY(height - y1Initial - 35);
		rectEnclosingCannon1 = new Rectangle(x1Initial - 25, height - y1Initial - 35, 50, 49.5);

		// Sun
		Image sunImage = new Image("Sun.png");
		ImageView ivSun = new ImageView();
		ivSun.setImage(sunImage);
		ivSun.setFitWidth(50);
		ivSun.setPreserveRatio(true);
		ivSun.setSmooth(true);
		ivSun.setCache(true);
		ivSun.setX(40);
		ivSun.setY(30);

		// You won label centered in the center of Screen
		youWonLabel.setVisible(false);
		youWonLabel.layoutXProperty().bind(pane.widthProperty().subtract(youWonLabel.widthProperty()).divide(2));
		youWonLabel.layoutYProperty().bind(pane.heightProperty().subtract(youWonLabel.heightProperty()).divide(2));
		youWonLabel.setFont(new Font(50));

		// Cloud 1
		Image cloud1Image = new Image("cloud1.png");
		ImageView ivCloud1 = new ImageView();
		ivCloud1.setImage(cloud1Image);
		ivCloud1.setFitWidth(50);
		ivCloud1.setPreserveRatio(true);
		ivCloud1.setSmooth(true);
		ivCloud1.setCache(true);
		ivCloud1.setX(10);
		ivCloud1.setY(50);

		// Cloud 2
		Image cloud2Image = new Image("cloud2.png");
		ImageView ivCloud2 = new ImageView();
		ivCloud2.setImage(cloud2Image);
		ivCloud2.setFitWidth(50);
		ivCloud2.setPreserveRatio(true);
		ivCloud2.setSmooth(true);
		ivCloud2.setCache(true);
		ivCloud2.setX(width / 4);
		ivCloud2.setY(35);

		// Player 2's cannon
		Image cannon2Image = new Image("cannon2.png");
		ivCannon2 = new ImageView();
		ivCannon2.setImage(cannon2Image);
		ivCannon2.setFitWidth(50);
		ivCannon2.setPreserveRatio(true);
		ivCannon2.setSmooth(true);
		ivCannon2.setCache(true);
		ivCannon2.setX(x2Initial - 25);
		ivCannon2.setY(height - y2Initial - 35);
		rectEnclosingCannon2 = new Rectangle(x2Initial - 25, height - y2Initial - 35, 50, 49.5);

		// Cloud 1's Animation
		PathTransition ptCloud1 = new PathTransition();
		Line line1 = new Line(0, height / 8, width, height / 8);
		ptCloud1.setNode(ivCloud1);
		ptCloud1.setPath(line1);
		ptCloud1.setDuration(Duration.seconds(25));
		ptCloud1.setCycleCount(Timeline.INDEFINITE);
		ptCloud1.setAutoReverse(true);
		ptCloud1.play();

		// Cloud 2's Animation
		PathTransition ptCloud2 = new PathTransition();
		Line line2 = new Line(width / 4, height / 9, height / 9, 35);
		ptCloud2.setNode(ivCloud2);
		ptCloud2.setPath(line2);
		ptCloud2.setDuration(Duration.seconds(40));
		ptCloud2.setCycleCount(Timeline.INDEFINITE);
		ptCloud2.setAutoReverse(true);
		ptCloud2.play();

		// Ground
		Rectangle rectangle = new Rectangle(width/2, height - y2Initial + 2, width + 50, height);
		rectangle.setFill(Color.rgb(0, 200, 48));
		Rectangle rectangle2 = new Rectangle(0, height - y1Initial + 2, width/2, height);
		rectangle2.setFill(Color.rgb(0, 200, 48));

		// Hill
//		hill = new Circle(width / 2, height - y2Initial, 60, Color.rgb(0, 200, 48));

		// Grid pane for aiming properties
		gPane = new GridPane();
		gPane.setLayoutX(width / 2);
		gPane.setLayoutY(height / 10);

		Label lbVelocity = new Label("Velocity");
		Label lbAngle = new Label("Angle");

		Button plusButtonAngle = new Button("+");
		plusButtonAngle.setPrefWidth(30);
		Button plusButtonVelocity = new Button("+");
		plusButtonVelocity.setPrefWidth(30);
		Button minusButtonAngle = new Button("-");
		minusButtonAngle.setPrefWidth(30);
		Button minusButtonVelocity = new Button("-");
		minusButtonVelocity.setPrefWidth(30);

		RadioButton rd01 = new RadioButton("0.1");
		RadioButton rd1 = new RadioButton("1");
		RadioButton rd10 = new RadioButton("10");

		ToggleGroup tg = new ToggleGroup();
		rd01.setToggleGroup(tg);
		rd1.setToggleGroup(tg);
		rd10.setToggleGroup(tg);

		rd1.setSelected(true);

		TextField tfVelocity = new TextField("2000");
		tfVelocity.setMaxWidth(40);
		TextField tfAngle = new TextField("45");
		tfAngle.setMaxWidth(40);
		tfVelocity.setEditable(false);
		tfAngle.setEditable(false);

		lbPlayer = new Label("Player 1");
		lbPlayer.setTextFill(Color.RED);
		lb2Player = new Label("Player 1");
		lb2Player.setTextFill(Color.RED);

		plusButtonAngle.setOnAction(e -> {
			double oldAngle = Double.parseDouble(tfAngle.getText());
			double newAngle = 0;

			if (rd01.isSelected()) {
				newAngle = oldAngle + 0.1;
			}

			if (rd1.isSelected()) {
				newAngle = oldAngle + 1;
			}
			if (rd10.isSelected()) {
				newAngle = oldAngle + 10;
			}

			if (newAngle <= 90) {
				tfAngle.setText(newAngle + "");
			}

		});

		minusButtonAngle.setOnAction(e -> {
			double oldAngle = Double.parseDouble(tfAngle.getText());
			double newAngle = 0;

			if (rd01.isSelected()) {
				newAngle = oldAngle - 0.1;
			}

			if (rd1.isSelected()) {
				newAngle = oldAngle - 1;
			}
			if (rd10.isSelected()) {
				newAngle = oldAngle - 10;
			}

			if (newAngle >= 0) {
				tfAngle.setText(newAngle + "");
			}

		});

		plusButtonVelocity.setOnAction(e -> {
			double oldVelocity = Double.parseDouble(tfVelocity.getText());
			double newVelocity = 0;
			if (rd01.isSelected()) {
				newVelocity = oldVelocity + 0.1;
			}
			if (rd1.isSelected()) {
				newVelocity = oldVelocity + 1;
			}
			if (rd10.isSelected()) {
				newVelocity = oldVelocity + 10;
			}
			if (newVelocity <= 3000) {
				tfVelocity.setText(newVelocity + "");
			}
		});

		minusButtonVelocity.setOnAction(e -> {
			double oldVelocity = Double.parseDouble(tfVelocity.getText());
			double newVelocity = 0;

			if (rd01.isSelected()) {
				newVelocity = oldVelocity - 0.1;
			}

			if (rd1.isSelected()) {
				newVelocity = oldVelocity - 1;
			}
			if (rd10.isSelected()) {
				newVelocity = oldVelocity - 10;
			}

			if (newVelocity >= 0) {
				tfVelocity.setText(newVelocity + "");
			}

		});

		btFire = new Button("Fire");
		btFire.setOnAction(e -> {
			try {
				fire(Double.parseDouble(tfAngle.getText()), Double.parseDouble(tfVelocity.getText()), MASS_CONSTANT);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		gPane.add(lbAngle, 0, 0);
		gPane.add(lbVelocity, 0, 1);
		gPane.add(lbPlayer, 1, 2);

		gPane.add(plusButtonAngle, 1, 0);
		gPane.add(plusButtonVelocity, 1, 1);

		gPane.add(tfAngle, 2, 0);
		gPane.add(tfVelocity, 2, 1);

		gPane.add(minusButtonAngle, 3, 0);
		gPane.add(minusButtonVelocity, 3, 1);

		gPane.add(lb2Player, 3, 2);
		gPane.add(btFire, 2, 2);

		gPane.add(rd01, 4, 0);
		gPane.add(rd1, 4, 1);
		gPane.add(rd10, 4, 2);

		gPane.setVgap(5);
		gPane.setHgap(5);

		quad = new QuadCurve();
		quad.setStartX(width / 4);
		quad.setStartY(height - y1Initial + 30);
		quad.setEndX((3 * width) / 4);
		quad.setEndY(height - y2Initial + 30);
		quad.setControlX(width / 2);
		quad.setControlY(height / 5);
		quad.setFill(Color.rgb(0, 200, 48));

		pane.getChildren().addAll(ivSun, ivCloud1, ivCloud2, rectangle,rectangle2,  ivCannon1, ivCannon2,quad,gPane,youWonLabel);
		setCenter(pane);
	}

	private void fire(double theta, double initialVelocity, double mass) throws URISyntaxException {
		// if the platform is desktop, playing of sound is using mediaplayer, if
		// in android using the native code
		mp = new MediaPlayer[2];
		System.out.print(player1Turn + " here ");
		
		if (Platform.isDesktop()) {
			mp[0] = new MediaPlayer(new Media(getClass().getResource("/fire.mp3").toURI().toString()));
			mp[1] = new MediaPlayer(new Media(getClass().getResource("/explosion.mp3").toURI().toString()));
		}
		double x, y,y2;
		playFire();
		gPane.setVisible(false);
		Circle ball = new Circle(RADIUS, RADIUS, RADIUS, Color.RED);
		if (player1Turn) {
			x = x1Initial;
			y = height - y1Initial;
			y2 = height - y2Initial ;
		} else {
			x = x2Initial;
			y = height - y2Initial;
			y2 = height - y1Initial ;
		}
		Projectile projectile = new Projectile(x, y, theta, initialVelocity, mass, player1Turn, width, height,y2);
		// toggle player1Turn
		player1Turn = (!player1Turn);
		
		PathTransition pt = new PathTransition();
		Polyline polyline = projectile.getPolyLine(50);
		pt.setNode(ball);
		pt.setPath(polyline);
		pt.setDuration(Duration.seconds(2.0));
		pt.play();

		// When the ball touches the ground
		pt.setOnFinished(e -> {
			playExplosion();
			ball.setVisible(false);
			
			nextTurn();
		});

		pt.currentTimeProperty().addListener(e -> {
			Shape shape = Shape.intersect(ball, rectEnclosingCannon2);
			Shape shape1 = Shape.intersect(ball, rectEnclosingCannon1);

			// Check if player 1 hit player 2's cannon
			if (shape.getBoundsInLocal().getWidth() != -1) {
				if (!player1Turn) {
					playExplosion();
					System.out.println("bom");
					ivCannon2.setVisible(false);
					youWonLabel.setText("Player 1 Won");
					youWonLabel.setTextFill(Color.RED);
					youWonLabel.setVisible(true);
					gPane.setVisible(false);
					return;
				}
			}
			// Check if player 2 hit player 1's cannon
			if (shape1.getBoundsInLocal().getWidth() != -1) {
				if (player1Turn) {
					playExplosion();
					System.out.println("bom");
					ivCannon1.setVisible(false);
					youWonLabel.setText("Player 2 Won");
					youWonLabel.setTextFill(Color.BLUE);
					youWonLabel.setVisible(true);
					gPane.setVisible(false);
					return;
				}
			}
			// Check if players hit the hill
			Shape shape2 = Shape.intersect(ball, quad);
			if (shape2.getBoundsInLocal().getWidth() != -1) {
				pt.stop();
				playExplosion();
				ball.setVisible(false);
				nextTurn();
				
			}

		});
		pane.getChildren().add(ball);

	}

	private void playFire() {
		if (Platform.isAndroid()) {
			service.play("fire");
		} else {
			mp[0].play();
		}
	}

	private void playExplosion() {
		if (Platform.isAndroid()) {
			service.play("explosion");
		} else {
			mp[1].play();
		}
	}
	
	
	private void nextTurn(){
		if (!youWonLabel.isVisible()) {
			gPane.setVisible(true);
		}
		if (player1Turn) {
			lbPlayer.setText("Player 1");
			lbPlayer.setTextFill(Color.RED);
			lb2Player.setText("Player 1");
			lb2Player.setTextFill(Color.RED);
		} else {
			lbPlayer.setText("Player 2");
			lbPlayer.setTextFill(Color.BLUE);
			lb2Player.setText("Player 2");
			lb2Player.setTextFill(Color.BLUE);
		}
	}

	@Override
	protected void updateAppBar(AppBar appBar) {
		appBar.setTitleText("BANG BANG");
		appBar.getActionItems().add(MaterialDesignIcon.LOCATION_SEARCHING.button(e -> System.out.println("AIM")));
	}

}
