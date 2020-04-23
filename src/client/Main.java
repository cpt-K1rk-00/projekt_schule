package client;

import java.text.DecimalFormat;
import java.util.Optional;

import javax.swing.GroupLayout.Alignment;

import com.sun.java.accessibility.util.GUIInitializedListener;

import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jdk.nashorn.internal.ir.SetSplitState;

/**
 * Die Klasse erzeugt die GUI und startet die Client-Anwendung.
 * 
 * @author Jan
 *
 */

public class Main extends Application {

	private GameClient client;
	Button[][] board = new Button[3][3];

	private Scene aktScene; // Speichert die aktuell genutzte Scene
	private Stage mainStage;
	int x = 600;
	int y = 400;

	// Login Seite
	private Button loginButton;
	private Button registerButton;

	// Register Seite
	private Button startRegistration;
	private TextField inputUsername;
	private TextField inputPassword;

	// main seite
	private VBox friendBox;
	private BorderPane root;
	private Label headlineLeague;
	private GridPane headlineStore;
	private Label headlineFriends;

	private final String cssStyle = "-fx-background-color: \r\n" + "        linear-gradient(#ffd65b, #e68400),\r\n"
			+ "        linear-gradient(#ffef84, #f2ba44),\r\n" + "        linear-gradient(#ffea6a, #efaa22),\r\n"
			+ "        linear-gradient(#ffe657 0%, #f8c202 50%, #eea10b 100%),\r\n"
			+ "        linear-gradient(from 0% 0% to 15% 50%, rgba(255,255,255,0.9), rgba(255,255,255,0));\r\n"
			+ "    -fx-background-radius: 30;\r\n" + "    -fx-background-insets: 0,1,2,3,0;\r\n"
			+ "    -fx-text-fill: #654b00;\r\n" + "    -fx-font-weight: bold;\r\n" + "    -fx-font-size: 14px;\r\n"
			+ "    -fx-padding: 10 20 10 20;";
	private final String cssFriendGreen = " -fx-background-color: linear-gradient(#f0ff35, #a9ff00);\r\n"
			+ "    -fx-background-radius: 1;\r\n" + "    -fx-background-insets: 0;\r\n" + "    -fx-text-fill: black;";
	private final String cssFriendRed = "-fx-background-color: linear-gradient(#ff5400, #be1d00);\r\n"
			+ "    -fx-background-radius: 1;\r\n" + "    -fx-background-insets: 0;\r\n" + "    -fx-text-fill: white;";

	/*
	 * Startet die Anwendung und erzeugt die GUI.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		client = new GameClient(this);
		// MainStage zuweisen
		mainStage = primaryStage;
		mainStage.setTitle("g&f");
		mainStage.setResizable(false);
		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
		// Login erzeugen
		mainStage.setScene(setLoginScene());
		mainStage.show();
	}

	public Scene setGameScene(String boardAsString, String opponent, boolean yourTurn) {
		if (yourTurn) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("Your Turn");
			alert.setTitle("info");
			alert.showAndWait();
		}
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				final int tmpX = x;
				final int tmpY = y;
				board[y][x] = new Button();
				board[y][x].setMinSize(200, 200);
				char symbol = boardAsString.charAt(3 * y + x);
				if (symbol == '#') {
					board[y][x].setText("");
					if (yourTurn) {
						board[y][x].setOnAction(evt -> {
							client.sendTurn(tmpX, tmpY);
						});
					}
				} else {
					if (symbol == 'x') {
						Image img = new Image(ResourceLoader.load("cross.jpg"));
						board[y][x].setGraphic(new ImageView(img));
					} else {
						Image img = new Image(ResourceLoader.load("circle.png"));
						board[y][x].setGraphic(new ImageView(img));
					}
				}
			}
		}
		GridPane root = new GridPane();
		root.setMinSize(600, 600);
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				root.add(board[y][x], x, y);
			}
		}
		return new Scene(root, 600, 600);
	}

	/**
	 * Die Methode gibt die Scene f�r einen Login zur�ck.
	 */
	public Scene setLoginScene() {
		Scene aktScene = new Scene(new Label(""), x, y);
		// �berschrift erstellen
		Label headline1 = new Label("Games &");
		headline1.setFont(new Font("Cambria", 100));
		headline1.setStyle("-fx-background-color:POWDERBLUE");
		Label headline2 = new Label("Friends");
		headline2.setFont(new Font("Cambria", 100));
		headline2.setStyle("-fx-background-color:POWDERBLUE");
		VBox subBoxHeadline = new VBox();
		subBoxHeadline.getChildren().addAll(headline1, headline2);
		subBoxHeadline.setAlignment(Pos.CENTER);
		// Buttons erzeugen
		loginButton = new Button();
		loginButton.setText("Log in");
		loginButton.setMinWidth(aktScene.getWidth() * 0.5);
		loginButton.setFont(new Font("Cambria", 25));
		loginButton.setStyle(cssStyle);
		loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				updateScene(setLoginInput());
			}
		});
		registerButton = new Button();
		registerButton.setText("register");
		registerButton.setMinWidth(aktScene.getWidth() * 0.5);
		registerButton.setStyle(cssStyle);
		registerButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				updateScene(setRegisterScene());

			}
		});
		VBox store = new VBox();
		store.getChildren().add(subBoxHeadline);
		store.getChildren().add(loginButton);
		store.getChildren().add(registerButton);
		store.setAlignment(Pos.CENTER);
		store.setSpacing(10);
		store.setStyle("-fx-background-color:POWDERBLUE");
		subBoxHeadline.setSpacing(0);
		Scene scene = new Scene(store, x, y);
		return scene;
	}

	/**
	 * Erzeugt die GUI, welche die Daten, die f�r eine Registrierug notwendig
	 * sind, einholt.
	 */
	public Scene setRegisterScene() {
		// Titel �ndern
		mainStage.setTitle("registration");
		// Seite erzeugen
		Label headline = new Label("registration");
		headline.setFont(new Font("Cambria", 100));
		Label username = new Label("username");
		username.setFont(new Font("Cambria", 40));
		inputUsername = new TextField();
		inputUsername.setFont(new Font("Cambria", 25));
		HBox storeUsername = new HBox();
		storeUsername.setSpacing(5);
		storeUsername.setAlignment(Pos.CENTER);
		storeUsername.getChildren().addAll(username, inputUsername);
		Label password = new Label("password");
		password.setFont(new Font("Cambria", 40));
		inputPassword = new TextField();
		inputPassword.setFont(new Font("Cambria", 25));
		HBox storePassword = new HBox();
		storePassword.setSpacing(5);
		storePassword.setAlignment(Pos.CENTER);
		storePassword.getChildren().addAll(password, inputPassword);
		registerButton = new Button("register");
		registerButton.setMinWidth(x * 0.5);
		registerButton.setStyle(cssStyle);
		registerButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				// Passwort und Username d�rfen nicht leer sein
				if (inputUsername.getText().equals("") || inputPassword.getText().equals("")) {
					showError("Username und Password d�rfen nicht leer sein");
				} else {
					client.register(inputUsername.getText(), inputPassword.getText());
				}
			}
		});
		Button backButton = new Button("back");
		backButton.setMinWidth(x * 0.5);
		backButton.setStyle(cssStyle);
		backButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				updateScene(setLoginScene());
			}
		});
		VBox store = new VBox();
		store.setAlignment(Pos.CENTER);
		store.setSpacing(10);
		store.getChildren().addAll(headline, storeUsername, storePassword, registerButton, backButton);
		store.setStyle("-fx-background-color:POWDERBLUE");
		Scene scene = new Scene(store, x, y);
		return scene;
	}

	/**
	 * Erzeugt die GUI, welche die Daten, die f�r eine Registrierug notwendig
	 * sind, einholt.
	 */
	public Scene setLoginInput() {
		// Titel �ndern
		mainStage.setTitle("Login");
		// Seite erzeugen
		Label headline = new Label("Login");
		headline.setFont(new Font("Cambria", 100));
		Label username = new Label("username");
		username.setFont(new Font("Cambria", 40));
		inputUsername = new TextField();
		inputUsername.setFont(new Font("Cambria", 25));
		HBox storeUsername = new HBox();
		storeUsername.setSpacing(5);
		storeUsername.setAlignment(Pos.CENTER);
		storeUsername.getChildren().addAll(username, inputUsername);
		Label password = new Label("password");
		password.setFont(new Font("Cambria", 40));
		inputPassword = new TextField();
		inputPassword.setFont(new Font("Cambria", 25));
		HBox storePassword = new HBox();
		storePassword.setSpacing(5);
		storePassword.setAlignment(Pos.CENTER);
		storePassword.getChildren().addAll(password, inputPassword);
		registerButton = new Button("login");
		registerButton.setMinWidth(x * 0.5);
		registerButton.setStyle(cssStyle);
		registerButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				if (inputUsername.getText().equals("") || inputPassword.getText().equals("")) {
					showError("Username und Password d�rfen nicht leer sein");
				} else {
					client.login(inputUsername.getText(), inputPassword.getText());
				}
			}
		});
		Button backButton = new Button("back");
		backButton.setMinWidth(x * 0.5);
		backButton.setStyle(cssStyle);
		backButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				updateScene(setLoginScene());
			}
		});
		VBox store = new VBox();
		store.setAlignment(Pos.CENTER);
		store.setSpacing(10);
		store.getChildren().addAll(headline, storeUsername, storePassword, registerButton, backButton);
		store.setStyle("-fx-background-color:POWDERBLUE");
		Scene scene = new Scene(store, x, y);
		return scene;
	}

	public Scene setStartScreen() {
		BorderPane root = new BorderPane();
		Image img = new Image(ResourceLoader.load("waiting.gif"));
		root.setCenter(new ImageView(img));

		return new Scene(root, 500, 500);
	}

	public void updateScene(Scene scene) {
		mainStage.setScene(scene);
	}

	/**
	 * Erzeugt das Main-Menu der Anwendung.
	 */
	public Scene setMainMenu() {
		// Bildschirmgr��e aktualisieren
		x = 700;
		y = 500;
		// Titel aktualisieren
		mainStage.setTitle("games & friends");
		// BorderPane einrichten
		root = new BorderPane();
		// �berschrift erzeugen
		headlineStore = new GridPane();
		headlineStore.setAlignment(Pos.CENTER);
		headlineLeague = new Label("Loading ..");
		headlineLeague.setFont(new Font("Cambria", 50));
		headlineLeague.setMinHeight(y * 0.2);
		headlineLeague.setMaxHeight(y * 0.2);
		headlineLeague.setMinWidth(x * 0.7);
		headlineLeague.setMaxWidth(x * 0.7);
		headlineFriends = new Label("Friends");
		headlineFriends.setMinHeight(y * 0.2);
		headlineFriends.setMaxHeight(y * 0.2);
		headlineFriends.setFont(new Font("Cambria", 50));
		headlineFriends.setAlignment(Pos.CENTER);
		headlineStore.add(headlineLeague, 0, 0);
		headlineStore.add(headlineFriends, 1, 0);
		// Buttons f�r Freund erzeugen
		VBox buttonsFriends = getFriends(null);
		ScrollPane scroll = new ScrollPane(buttonsFriends);
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		;
		scroll.setMaxHeight(y * 0.6);
		scroll.setMinHeight(y * 0.6);
		scroll.setStyle("-fx-background:POWDERBLUE; -fx-background-color:transparent;");
		scroll.setBorder(null);
		// Alle Buttons am Boden speichern
		// Spielen Button machen
		Button playButton = new Button("play");
		playButton.setMinHeight(y * 0.1);
		playButton.setMaxHeight(y * 0.1);
		playButton.setStyle(cssStyle);
		playButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				updateScene(setStartScreen());
				client.sendStartGame();
			}

		});
		// Liga verlassen/suchen hinzuf�gen
		Button manageLeague = new Button("leave/add");
		manageLeague.setMinHeight(y * 0.1);
		manageLeague.setMaxHeight(y * 0.1);
		manageLeague.setStyle(cssStyle);
		manageLeague.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				// Pr�fen, ob der Spieler in einer Liga ist
				if (client.getLeague().equals("keine Liga")) {
					// Liganamen abfragen
					client.getAllLeagues();
				} else {
					client.leaveLeague();
				}
			}
		});
		// Statistik aufrufen und verlassen
		Button stats = new Button("stats");
		stats.setMinHeight(y * 0.1);
		stats.setMaxHeight(y * 0.1);
		stats.setStyle(cssStyle);
		stats.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				client.send("DATAREQUEST:" + client.getUsername());
			}
		});
		Region buttonsRegion1 = new Region();
		HBox.setHgrow(buttonsRegion1, Priority.ALWAYS);
		Region buttonsRegion2 = new Region();
		HBox.setHgrow(buttonsRegion2, Priority.ALWAYS);
		Region buttonsRegion3 = new Region();
		HBox.setHgrow(buttonsRegion3, Priority.ALWAYS);
		HBox subButtonsStore = new HBox();
		subButtonsStore = new HBox();
		subButtonsStore.setAlignment(Pos.CENTER);
		subButtonsStore.setMinWidth(x * 0.6);
		subButtonsStore.setMaxWidth(x * 0.6);
		subButtonsStore.setSpacing(30);
		subButtonsStore.getChildren().addAll(playButton, manageLeague, stats);
		HBox buttonsStore = new HBox(subButtonsStore, buttonsRegion1, buttonsRegion2, buttonsRegion3);
		// Button symmetrisch unter der Liste erzeugen
		buttonsStore.setPadding(new Insets(y * 0.06, x * 0.3, 20, 0));
		// Der Wurzel hinzuf�gen
		root.setTop(headlineStore);
		ScrollPane leagueView = new ScrollPane(null);
		leagueView.setMinHeight(y * 0.6);
		leagueView.setMaxHeight(y * 0.6);
		leagueView.setHbarPolicy(ScrollBarPolicy.NEVER);
		leagueView.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		leagueView.setStyle("-fx-background:POWDERBLUE; -fx-background-color:transparent;");
		leagueView.setBorder(null);
		root.setCenter(leagueView);
		root.setRight(scroll);
		root.setBottom(buttonsStore);
		root.setStyle("-fx-background-color:POWDERBLUE;");
		Scene tmp = new Scene(root, x, y);
		return tmp;
	}

	/**
	 * Stellt alle Freunde als Liste dar. Freunde die online sind k�nnen
	 * herausgefordert werden. Freunde die offline/online sind k�nnen als Freunde
	 * entfernt werden.
	 * 
	 * @return
	 */
	public VBox getFriends(List<User> friends) {
		friendBox = new VBox();
		// Das Freunde hinzuf�gen
		HBox add = new HBox();
		TextField input = new TextField();
		Button doSearch = new Button("add");
		doSearch.setStyle("-fx-background-color: \r\n" + "        linear-gradient(#ffd65b, #e68400),\r\n"
				+ "        linear-gradient(#ffef84, #f2ba44),\r\n" + "        linear-gradient(#ffea6a, #efaa22),\r\n"
				+ "        linear-gradient(#ffe657 0%, #f8c202 50%, #eea10b 100%),\r\n"
				+ "        linear-gradient(from 0% 0% to 15% 50%, rgba(255,255,255,0.9), rgba(255,255,255,0));");
		doSearch.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (input.getText().equals("")) {
					showError("kein leeres Feld");
				} else {
					client.addFriend(input.getText());
				}
			}
		});
		input.setMinWidth(x * 0.25);
		add.getChildren().add(input);
		add.getChildren().add(doSearch);
		friendBox.getChildren().add(add);
		// Am anfang auf null setzen
		if (friends == null) {
			Label msg = new Label("Freunde nicht \n geladen");
			msg.setTextAlignment(TextAlignment.CENTER);
			msg.setFont(new Font("Cambria", 30));
			msg.setStyle("-fx-background-color:POWDERBLUE");
			friendBox.getChildren().add(msg);
			return friendBox;
		}
		friends.toFirst();
		// F�r jeden Freund einen Button erzeugen
		while (friends.hasAccess()) {
			Button aktButton = new Button(friends.getContent().getUsername());
			aktButton.setMinWidth(x * 0.3);
			// Pr�fen, ob freund online ist
			if (friends.getContent().isOnline()) {
				aktButton.setStyle(cssFriendGreen);
			} else {
				aktButton.setStyle(cssFriendRed);
			}
			aktButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					Button btn = (Button) event.getSource();
					// linksklick
					if (event.getButton() == MouseButton.PRIMARY) {
						if (btn.getStyle().equals(cssFriendGreen)) {
							// Open direkMessage Window
							int xTmp = x / 2;
							int yTmp = y / 6;
							Stage stage = new Stage();
							stage.setResizable(false);
							stage.initModality(Modality.WINDOW_MODAL);
							stage.initOwner(aktButton.getScene().getWindow());
							VBox root = new VBox();
							root.setAlignment(Pos.CENTER);
							root.setSpacing(yTmp * 0.025);
							root.setStyle("-fx-background:POWDERBLUE;");
							TextField input = new TextField();
							input.setPrefSize(xTmp - xTmp * 0.04, yTmp * 0.7);
							input.setText("max. 120");
							input.setTextFormatter(new TextFormatter<String>(
									change -> change.getControlNewText().length() <= 120 ? change : null));
							RadioButton safe = new RadioButton("safe");
							safe.setSelected(true);
							RadioButton unsafe = new RadioButton("unsafe");
							ToggleGroup radioGroup = new ToggleGroup();
							safe.setToggleGroup(radioGroup);
							unsafe.setToggleGroup(radioGroup);
							HBox store = new HBox(safe, unsafe);
							store.setStyle("-fx-background:POWDERBLUE;");
							Button send = new Button("send");
							send.setStyle(cssStyle);
							send.setPrefSize(xTmp * 0.25, yTmp * 0.25);
							send.setOnAction((evt) -> {
								if (safe.isSelected()) {
									String txt = input.getText().toLowerCase();
									if (justCharacter(txt)) {
										client.sendDirectMessage(txt, btn.getText(), true);
										stage.close();
									} else {
										showError("nur Buchstaben bitte");
									}
								} else {
									client.sendDirectMessage(input.getText(), btn.getText(), false);
									stage.close();
								}
							});
							root.getChildren().addAll(input,store,send);
							stage.setScene(new Scene(root, xTmp, yTmp));
							stage.showAndWait();
						}

					} else if (event.getButton() == MouseButton.SECONDARY) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Remove Friend");
						alert.setHeaderText("permission");
						alert.setContentText("Remove Friend?");
						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK) {
							client.removeFriend(client.getUsername(), btn.getText());
						}
					}
				}
			});
			friendBox.getChildren().add(aktButton);
			friends.next();
		}
		return friendBox;
	}

	public void setFriendBox(VBox friendBox) {
		this.friendBox = friendBox;
	}

	public Scene createStatisticView(String[] stats) {
		VBox root = new VBox();
		root.setStyle("-fx-background:POWDERBLUE;");
		root.setAlignment(Pos.CENTER);
		if (stats == null) {
			Label label = new Label("ERROR");
			label.setStyle("-fx-background:POWDERBLUE;");
			label.setMinWidth(x * 0.7);
			label.setTextAlignment(TextAlignment.CENTER);
			label.setFont(new Font("Cambria", 30));
			root.getChildren().add(label);
		}
		ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
		data.add(new PieChart.Data("wins", Double.parseDouble(stats[0])));
		data.add(new PieChart.Data("defeats", Double.parseDouble(stats[1])));
		data.add(new PieChart.Data("ties", Double.parseDouble(stats[2])));
		PieChart chart = new PieChart(data);
		chart.setPrefSize(x, y * 0.9);
		for (final PieChart.Data data2 : chart.getData()) {
			data2.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					DecimalFormat df = new DecimalFormat("0.00");
					double d = data2.getPieValue() / Double.parseDouble(stats[3]) * 100;
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setTitle("quote");
					alert.setGraphic(null);
					alert.setContentText(df.format(d));
					alert.showAndWait();
				}
			});
		}
		Button back = new Button("back");
		back.setStyle(cssStyle);
		back.setPrefHeight(y * 0.08);
		back.setOnAction((evt) -> {
			updateScene(setMainMenu());
			client.loadLeague(client.getUsername());
			client.getFriends(client.getUsername());
		});
		root.getChildren().add(chart);
		root.getChildren().add(back);
		return new Scene(root, x, y);
	}

	/**
	 * Erzeugt eine Darstellung der Liga in Tabellenform. Mitglieder der Liga die
	 * Online sind, werden gr�n angezeigt und k�nnen herausgefordert werden.
	 * 
	 * @return
	 */
	public VBox createLeagueView(List<String> data) {
		// Erstellt die VBox in der die Liga-Tabelle gezeigt wird
		VBox table = new VBox();
		data.toFirst();
		int index = 1;
		while (data.hasAccess()) {
			String[] inf = data.getContent().split(";");
			// Die Datenspeichern
			HBox row = new HBox();
			row.setMinHeight(y * 0.05);
			// Platz anzeigen
			Label place = new Label(new Integer(index).toString() + ".");
			place.setMinWidth(x * 0.1);
			place.setFont(new Font("Cambria", 20));
			place.setStyle("-fx-background-color:POWDERBLUE");
			Button btn = new Button(inf[0]);
			btn.setMinWidth(x * 0.5);
			if (inf[2].equals("1")) {
				btn.setStyle(cssFriendGreen);
			} else {
				btn.setStyle(cssFriendRed);
			}
			Label points = new Label(inf[1]);
			points.setMinWidth(x * 0.1);
			points.setFont(new Font("Cambria", 20));
			points.setStyle("-fx-background-color:POWDERBLUE");
			points.setAlignment(Pos.CENTER);
			row.getChildren().addAll(place, btn, points);
			table.getChildren().add(row);
			index++;
			data.next();
		}
		// Die Tabelle hinzeigen
		return table;
	}

	public VBox createLeagueList(List<String> names) {
		VBox leagueBox = new VBox();
		// Am anfang auf null setzen
		if (names == null) {
			Label msg = new Label("Ligen nicht geladen");
			msg.setMinWidth(x * 0.7);
			msg.setTextAlignment(TextAlignment.CENTER);
			msg.setFont(new Font("Cambria", 30));
			msg.setStyle("-fx-background-color:POWDERBLUE");
			leagueBox.getChildren().add(msg);
			return leagueBox;
		}
		names.toFirst();
		// F�r jede Liga einen Button erzeugen
		while (names.hasAccess()) {
			Button aktButton = new Button(names.getContent());
			aktButton.setMinWidth(x * 0.7);
			aktButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					Button btn = (Button) event.getSource();
					client.joinLeague(btn.getText());
				}
			});
			leagueBox.getChildren().add(aktButton);
			names.next();
		}
		return leagueBox;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public BorderPane getRoot() {
		return this.root;
	}

	public Label getHeadlineLeague() {
		return this.headlineLeague;
	}

	public GridPane getHeadlineStore() {
		return this.headlineStore;
	}

	public void setHeadlineLeague(Label label) {
		this.headlineLeague = label;
	}

	public Label getHeadlineFriends() {
		return this.headlineFriends;
	}

	// Hilsmethoden, die eine Datenbankabfrage darstellen

	/**
	 * Freunde aus der Datenbank hinzuholen. Die Freunde die online sind zuerst
	 * hinzuf�gen. Anfrage an den Server schicken. .
	 * 
	 * @return
	 */
	public List<User> getFriendsNames() {
		List<User> result = new List<User>();
		result.append(new User("ficker02", true));
		result.append(new User("dude", true));
		result.append(new User("jop", true));
		result.append(new User("name1", true));
		result.append(new User("stopper02", true));
		result.append(new User("git02", true));
		result.append(new User("destructor", false));
		result.append(new User("killer", false));
		result.append(new User("name12", false));
		result.append(new User("name123", false));
		for (int i = 0; i < 10; i++) {
			result.append(new User(new Integer(i).toString(), false));
		}
		return result;
	}

	public List<String> getLeagueData() {
		List<String> result = new List<String>();
		for (int i = 0; i < 30; i++) {
			result.append("asdf:1");
		}
		return result;
	}

	/**
	 * Macht Error-Message.
	 * 
	 * @param pMessage
	 */
	public void showError(String pMessage) {
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setHeaderText("Error");
		errorAlert.setContentText(pMessage);
		errorAlert.showAndWait();
	}

	public boolean justCharacter(String pMessage) {
		char[] all = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'x', 'y', 'z' };
		for (char c : pMessage.toCharArray()) {
			boolean tmp = false;
			for (char c2 : all) {
				if (c == c2) {
					tmp = true;
				}
			}
			if (!tmp) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
