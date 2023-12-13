module com.imfjguillenb.proyectos.Kefsi01 {
    requires transitive javafx.controls;
    requires javafx.fxml;
	requires java.sql;

    opens com.imfjguillenb.proyectos.Kefsi01 to javafx.fxml;
    exports com.imfjguillenb.proyectos.Kefsi01;
}
