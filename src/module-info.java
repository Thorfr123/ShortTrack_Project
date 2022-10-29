module TesteJavaFX {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.base;
	
	opens gui to javafx.graphics, javafx.fxml;
}
