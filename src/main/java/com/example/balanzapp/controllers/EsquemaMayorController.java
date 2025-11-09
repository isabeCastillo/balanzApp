package com.example.balanzapp.controllers;

import com.example.balanzapp.Conexion.ConexionDB;
import com.example.balanzapp.dao.MayorDAO;
import com.example.balanzapp.models.MovimientoMayor;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EsquemaMayorController {

    @FXML private Label lblTitulo;
    @FXML private Label lblNombreYTipoCuenta;
    @FXML private Label lblSaldo;

    @FXML private ComboBox<Integer> comboMesInicio;
    @FXML private ComboBox<Integer> comboAnioInicio;
    @FXML private ComboBox<Integer> comboMesFin;
    @FXML private ComboBox<Integer> comboAnioFin;

    @FXML private Button btnCargar;

    @FXML private ListView<String> listDebe;
    @FXML private ListView<String> listHaber;

    // cuenta que se va a mayorizar
    private int idCuentaActual = -1;

    @FXML
    private void initialize() {
        inicializarMeses();
        inicializarAnios();
        cargarCuentaPorDefecto(); // para que puedas "ver algo" sin wiring extra

        // seleccionar por defecto: mes / año actual
        LocalDate hoy = LocalDate.now();
        int mesActual = hoy.getMonthValue();
        int anioActual = hoy.getYear();

        if (comboMesInicio.getItems().contains(mesActual)) comboMesInicio.setValue(mesActual);
        if (comboMesFin.getItems().contains(mesActual)) comboMesFin.setValue(mesActual);
        if (comboAnioInicio.getItems().contains(anioActual)) comboAnioInicio.setValue(anioActual);
        if (comboAnioFin.getItems().contains(anioActual)) comboAnioFin.setValue(anioActual);

        btnCargar.setOnAction(e -> cargarMovimientos());

        // cargar de entrada
        cargarMovimientos();
    }

    private void inicializarMeses() {
        for (int m = 1; m <= 12; m++) {
            comboMesInicio.getItems().add(m);
            comboMesFin.getItems().add(m);
        }
    }

    private void inicializarAnios() {
        String sql = "SELECT DISTINCT EXTRACT(YEAR FROM fecha) AS anio FROM tbl_partidas ORDER BY anio";
        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int anio = (int) rs.getDouble("anio");
                comboAnioInicio.getItems().add(anio);
                comboAnioFin.getItems().add(anio);
            }
        } catch (SQLException e) {
            System.out.println("Error cargando años: " + e.getMessage());
        }

        // por si acaso no hay registros, usar año actual
        if (comboAnioInicio.getItems().isEmpty()) {
            int anioActual = LocalDate.now().getYear();
            comboAnioInicio.getItems().add(anioActual);
            comboAnioFin.getItems().add(anioActual);
        }
    }

    /**
     * Carga la primera cuenta de la BD como cuenta por defecto.
     * Así puedes ver el esquema aunque aún no pases parámetros desde otra pantalla.
     */
    private void cargarCuentaPorDefecto() {
        String sql = "SELECT id_cuenta, nombre, tipo FROM tbl_cntaContables ORDER BY id_cuenta LIMIT 1";
        try (Connection conn = ConexionDB.connection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                idCuentaActual = rs.getInt("id_cuenta");
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");
                lblNombreYTipoCuenta.setText(nombre + " - " + tipo);
            } else {
                lblNombreYTipoCuenta.setText("SIN CUENTAS DEFINIDAS");
            }
        } catch (SQLException e) {
            System.out.println("Error cargando cuenta por defecto: " + e.getMessage());
            lblNombreYTipoCuenta.setText("ERROR AL CARGAR CUENTA");
        }
    }

    /**
     * Si más adelante quieres abrir este esquema desde otra pantalla
     * y pasarle la cuenta, puedes usar este método.
     */
    public void setCuenta(int idCuenta, String nombreCuenta, String tipoCuenta) {
        this.idCuentaActual = idCuenta;
        lblNombreYTipoCuenta.setText(nombreCuenta + " - " + tipoCuenta);
        cargarMovimientos();
    }

    private void cargarMovimientos() {
        if (idCuentaActual <= 0) {
            mostrarAlerta("Error", "No hay cuenta seleccionada para mayorizar.");
            return;
        }

        Integer mesIni = comboMesInicio.getValue();
        Integer mesFin = comboMesFin.getValue();
        Integer anioIni = comboAnioInicio.getValue();
        Integer anioFin = comboAnioFin.getValue();

        if (mesIni == null || mesFin == null || anioIni == null || anioFin == null) {
            mostrarAlerta("Error", "Selecciona el rango de meses y años.");
            return;
        }

        // construir fechas (día 1 del mes inicio, último día del mes fin)
        LocalDate desde = LocalDate.of(anioIni, mesIni, 1);
        LocalDate hasta = LocalDate.of(anioFin, mesFin, 1)
                .withDayOfMonth(LocalDate.of(anioFin, mesFin, 1).lengthOfMonth());

        if (hasta.isBefore(desde)) {
            mostrarAlerta("Error", "La fecha final no puede ser menor que la inicial.");
            return;
        }

        List<MovimientoMayor> lista = MayorDAO.obtenerMayorPorCuentaYRango(
                idCuentaActual, desde, hasta
        );

        listDebe.getItems().clear();
        listHaber.getItems().clear();

        if (lista.isEmpty()) {
            lblSaldo.setText("SALDO: $0.00");
            mostrarInfo("Información", "No hay movimientos para esa cuenta en el rango seleccionado.");
            return;
        }

        double saldoFinal = 0.0;

        for (MovimientoMayor mov : lista) {
            // agregamos una línea a Debe u Haber según corresponda
            if (mov.getDebe() > 0) {
                String lineaDebe = String.format("%s | %s | %.2f",
                        mov.getFecha() != null ? mov.getFecha() : "",
                        mov.getConcepto() != null ? mov.getConcepto() : "",
                        mov.getDebe());
                listDebe.getItems().add(lineaDebe);
            } else {
                listDebe.getItems().add(""); // para mantener el "renglón" alineado
            }

            if (mov.getHaber() > 0) {
                String lineaHaber = String.format("%s | %s | %.2f",
                        mov.getFecha() != null ? mov.getFecha() : "",
                        mov.getConcepto() != null ? mov.getConcepto() : "",
                        mov.getHaber());
                listHaber.getItems().add(lineaHaber);
            } else {
                listHaber.getItems().add("");
            }

            saldoFinal = mov.getSaldo();
        }

        lblSaldo.setText(String.format("SALDO: $%.2f", saldoFinal));
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
