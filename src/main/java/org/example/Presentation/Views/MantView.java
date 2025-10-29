package org.example.Presentation.Views;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import org.example.dtos.mantemiento.MantResponseDto;
import org.example.Presentation.Models.MantTableModel;

import java.awt.*;
import java.util.Date;

public class MantView {
    private JPanel ContentPanel;
    private JTable MantTable;
    private JScrollPane MantTableScroll;
    private JButton AgregarButton;
    private JButton BorrarButton;
    private JButton UpdateButton;
    private JButton ClearButton;
    private JTextField DescripcionTextField;
    private JTextField TipoTextField;
    private JPanel datePickerPanel;
    private JDateChooser dateChooser;

    private final MantTableModel tableModel;
    private final LoadingOverlay loadingOverlay;

    public MantView(JFrame parentFrame) {
        tableModel = new MantTableModel();
        MantTable.setModel(tableModel);
        initDatePickers();
        // Reusable overlay instance
        loadingOverlay = new LoadingOverlay(parentFrame);
    }

    /**
     * Shows or hides the loading overlay.
     */
    public void showLoading(boolean visible) {
        loadingOverlay.show(visible);
    }

    // --- getters ---
    public MantTableModel getTableModel() { return tableModel; }
    public JPanel getContentPanel() { return ContentPanel; }
    public JTable getMantTable() { return MantTable; }
    public JButton getAgregarButton() { return AgregarButton; }
    public JButton getBorrarButton() { return BorrarButton; }
    public JButton getUpdateButton() { return UpdateButton; }
    public JButton getClearButton() { return ClearButton; }
    public JTextField getMantDescField() { return DescripcionTextField; }
    public JTextField getMantTipoField() { return TipoTextField; }

    // --- nuevos métodos para la fecha ---
    public Date getSelectedDate() {
        return dateChooser.getDate();
    }

    public void setSelectedDate(Date date) {
        dateChooser.setDate(date);
    }

    // --- helper methods ---
    public void clearFields() {
        DescripcionTextField.setText("");
        TipoTextField.setText("");
        MantTable.clearSelection();
        dateChooser.setDate(new Date()); // Reinicia la fecha al día actual
    }

    public void populateFields(MantResponseDto mant) {
        DescripcionTextField.setText(mant.getDescripcion());
        TipoTextField.setText(mant.getTipo());
        if (mant.getFecha() != null) {
            dateChooser.setDate(mant.getFecha());
        } else {
            dateChooser.setDate(new Date());
        }
    }

    private void initDatePickers() {
        datePickerPanel.setLayout(new BorderLayout());
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new Date());
        datePickerPanel.add(dateChooser, BorderLayout.CENTER);
        datePickerPanel.revalidate();
        datePickerPanel.repaint();
    }
}

