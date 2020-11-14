package sd_GUI;
import java.beans.EventHandler;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import javax.script.Bindings;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

//アプリケーションスレッド
public class Stopwatch extends Application{


	private int hour = 0;
	private int minute = 0;
	private int second = 0;
	private int ms = 0;
	private String LBString = "スタート";
	private int lapNumber = 1;
	private double speed = 1.0;
	private String RBString = "リセット";
	private ObservableList<Lap> data =
			FXCollections.observableArrayList();
	private final TableView<Lap> table = new TableView<>();
	private PlatformExample pe;


	@Override
	public void start(Stage pstage) {
		//TableViewの設定
		table.setEditable(true);
		table.setMaxHeight(180);
		table.setMaxWidth(320);
		//columnの設定
		TableColumn Col1 = new TableColumn("Lap");
		Col1.setMinWidth(50);
		Col1.setCellValueFactory(
				new PropertyValueFactory<>("Lap"));
		TableColumn Col2 = new TableColumn("Time");
		Col2.setMinWidth(160);
		Col2.setCellValueFactory(
				new PropertyValueFactory<>("Time"));
		TableColumn Col3 = new TableColumn("Speed");
		Col3.setMinWidth(50);
		Col3.setCellValueFactory(
				new PropertyValueFactory<>("Speed"));
		table.getColumns().addAll(Col1,Col2,Col3);
		//sliderの生成
		Slider SpeedSlider = new Slider(0, 2, 1);
		setSlider(SpeedSlider,0.2,false);
		//Labelの生成
		Label TimeView = new Label("00：00：00.00");
		TimeView.setFont(Font.font("Serif",50));
		Label SpeedLabel = new Label("速度",SpeedSlider);
		SpeedLabel.setContentDisplay(ContentDisplay.RIGHT);
		Button SpeedReset = new Button("リセット");
		HBox SpeedView = new HBox(SpeedLabel,SpeedReset);
		SpeedView.setSpacing(8);
		//Buttonの生成
		Button LeftButton = new Button(LBString);
		Button RightButton = new Button(RBString);
		//レイアウトコンテナの作成
		HBox TimeContent = new HBox(TimeView);
		TimeContent.setPadding(new Insets(40,20,40,40));
		HBox ButtonContent = new HBox(LeftButton,RightButton);
		ButtonContent.setSpacing(40);
		ButtonContent.setPadding(new Insets(0,0,30,110));
		HBox TableContent = new HBox(table);
		TableContent.setPadding(new Insets(20,0,0,45));
		VBox root = new VBox(10);
		root.setPadding(new Insets(20,30,20,30));
		root.getChildren().addAll(TimeContent,ButtonContent,SpeedView,TableContent);
		Scene scene = new Scene(root);
		pstage.setTitle("StopWatch");
		pstage.setScene(scene);
		pstage.show();
		pe = new PlatformExample();
		pe.start(pstage,TimeView);

		//イベント
		LeftButton.setOnAction((ActionEvent e) -> {
			if(LBString.equals("スタート")) {
				pe.Latch.countDown();
				LBString = "ストップ";
				RBString = "ラップ";
				LeftButton.setText(LBString);
				RightButton.setText(RBString);
			}else if(LBString.equals("ストップ")) {
				pe.stop();
				pe = new PlatformExample();
				pe.start(pstage,TimeView);
				LBString = "再開";
				RBString = "リセット";
				LeftButton.setText(LBString);
				RightButton.setText(RBString);
			}else if(LBString.equals("再開")) {
				pe.Latch.countDown();
				LBString = "ストップ";
				RBString = "ラップ";
				LeftButton.setText(LBString);
				RightButton.setText(RBString);
			}
		});
		RightButton.setOnAction((ActionEvent e) -> {
			if(RBString.equals("リセット") && LBString.equals("再開")) {
				pe.stop();
				pe = new PlatformExample();
				pe.start(pstage,TimeView);
				hour = 0;
				minute = 0;
				second = 0;
				ms = 0;
				TimeView.setText("00：00：00.00");
				LBString = "スタート";
				LeftButton.setText(LBString);
				RightButton.setText(RBString);
			}else if(RBString.equals("ラップ")) {
				BigDecimal value = new BigDecimal(speed).setScale(1,BigDecimal.ROUND_HALF_UP);
				data.add(new Lap(lapNumber,String.format("%02d", minute) + "：" + String.format("%02d", second)
				+ "：" + String.format("%02d", ms) + "." + String.format("%02d", ms),value.doubleValue()));
				lapNumber++;
				table.setItems(data);
			}
		});
		SpeedSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
			speed =  newValue.doubleValue();
		});
		SpeedReset.setOnAction((ActionEvent e) -> {
			speed = 1.0;
			SpeedSlider.setValue(1.0);
		});


		//終了
		pstage.setOnCloseRequest(event -> System.exit(0));
	}

	/**
	 * サイズの設定
	 * @param slider スライダ
	 * @param tickUnit 間隔
	 * @param isShow 目盛の表示
	 */
	void setSlider(Slider slider,double tickUnit,boolean isShow){
		slider.setPrefWidth(300);
		slider.setShowTickLabels(isShow);
		slider.setShowTickMarks(isShow);
		slider.setMajorTickUnit(tickUnit);
	}

	/**
	 * ラップタイムを保存しておくクラス
	 * @param LapN 番号
	 * @param lapT 時間
	 * @param lapS 速度
	 */
	public static class Lap{
		private final SimpleIntegerProperty Lap;
		private final SimpleStringProperty Time;
		private final SimpleDoubleProperty Speed;

		private Lap(Integer lapN,String lapT,Double lapS) {
			this.Lap = new SimpleIntegerProperty(lapN);
			this.Time = new SimpleStringProperty(lapT);
			this.Speed = new SimpleDoubleProperty(lapS);
		}
		public Integer getLap() {return Lap.get();}
		public void setLap(Integer lapN) {Lap.set(lapN);}
		public String getTime() {return Time.get();}
		public void setTime(String lapT) {Time.set(lapT);}
		public Double getSpeed() {return Speed.get();}
		public void setSpeed(Double lapS) {Speed.set(lapS);}

	}

	//mainメソッド
	public static void main(String[] args) {
		launch(args);
	}

	//タイマースレッド
	public class PlatformExample extends Application {
		private boolean isRunning = true;
		private CountDownLatch Latch = new CountDownLatch(1);
		@Override
		public void start(Stage primaryStage) {

		}
		public void start(Stage primaryStage,Label label){
			new Thread(() -> {
				try {
					Latch.await();
				} catch (InterruptedException e1) {}
				while (this.isRunning) {
					Platform.runLater(() -> {
						label.setText(String.format("%02d", hour) + "：" +String.format("%02d", minute) + "：" + String.format("%02d", second)
						+ "." + String.format("%02d", ms));
					});
					try {
						if(speed == 1.0)Thread.sleep((long)(9));
						else Thread.sleep((long)(10*(2.1 - speed)));
						ms++;
						if(ms >= 100) {
							ms = 0;
							second++;
						}
						if(second >= 60) {
							second = 0;
							minute++;
						}
						if(minute >= 60) {
							minute = 0;
							hour++;
						}
					} catch (InterruptedException e) { }
				}
			}).start();
			primaryStage.show();
		}
		public void stop() {
			this.isRunning = false;
		}
	}
}
