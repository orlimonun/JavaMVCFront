package org.example.Presentation.Controllers;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.List;
import java.util.Date;

import org.example.Presentation.Views.MantView;
import org.example.Services.MantService;
import org.example.Utilities.EventType;
import org.example.dtos.mantemiento.*;
import org.example.Presentation.Observable;

public class MantController extends Observable {

    private final MantView mantView;
    private final MantService mantService;

    public MantController(MantView mantView, MantService mantService) {
        this.mantView = mantView;
        this.mantService = mantService;

        addObserver(mantView.getTableModel());
        loadMantAsync();
        addListeners();
    }

    private void loadMantAsync() {
        mantView.showLoading(true);

        SwingWorker<List<MantResponseDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MantResponseDto> doInBackground() throws Exception {
                return mantService.listMantAsync(1L).get();
            }

            @Override
            protected void done() {
                try {
                    List<MantResponseDto> mants = get();
                    mantView.getTableModel().setMants(mants);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mantView.showLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void addListeners() {
        mantView.getAgregarButton().addActionListener(e -> handleAddMant());
        mantView.getUpdateButton().addActionListener(e -> handleUpdateMant());
        mantView.getBorrarButton().addActionListener(e -> handleDeleteMant());
        mantView.getClearButton().addActionListener(e -> handleClearFields());
        mantView.getMantTable().getSelectionModel().addListSelectionListener(this::handleRowSelection);
    }

    // ---------------------------
    // Action Handlers
    // ---------------------------

    private void handleAddMant() {
        String descripcion = mantView.getMantDescField().getText().trim();
        String tipo = mantView.getMantTipoField().getText().trim();
        Date fecha = mantView.getSelectedDate();

        if (descripcion.isEmpty() || tipo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor complete todos los campos antes de agregar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AddMantRequestDto dto = new AddMantRequestDto(fecha, descripcion, tipo, 1L);

        mantView.showLoading(true);

        SwingWorker<MantResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected MantResponseDto doInBackground() throws Exception {
                return mantService.addMantAsync(dto, 1L).get();
            }

            @Override
            protected void done() {
                try {
                    MantResponseDto mant = get();
                    notifyObservers(EventType.CREATED, mant);
                    mantView.clearFields();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    mantView.showLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleUpdateMant() {
        int selectedRow = mantView.getMantTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un mantenimiento para actualizar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        MantResponseDto selectedMant = mantView.getTableModel().getMants().get(selectedRow);
        String descripcion = mantView.getMantDescField().getText().trim();
        String tipo = mantView.getMantTipoField().getText().trim();
        Date fecha = mantView.getSelectedDate();

        if (descripcion.isEmpty() || tipo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor complete todos los campos antes de actualizar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UpdateMantRequestDto dto = new UpdateMantRequestDto( fecha, descripcion, tipo, 1L);

        mantView.showLoading(true);

        SwingWorker<MantResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected MantResponseDto doInBackground() throws Exception {
                return mantService.updateMantAsync(dto, 1L).get();
            }

            @Override
            protected void done() {
                try {
                    MantResponseDto updatedMant = get();
                    notifyObservers(EventType.UPDATED, updatedMant);
                    mantView.clearFields();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    mantView.showLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleDeleteMant() {
        int selectedRow = mantView.getMantTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un mantenimiento para eliminar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        MantResponseDto selectedMant = mantView.getTableModel().getMants().get(selectedRow);
        DeleteMantRequestDto dto = new DeleteMantRequestDto(selectedMant.getId());

        int confirm = JOptionPane.showConfirmDialog(null, "¿Seguro que desea eliminar este mantenimiento?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        mantView.showLoading(true);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return mantService.deleteMantAsync(dto, 1L).get();
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) notifyObservers(EventType.DELETED, selectedMant.getId());
                    mantView.clearFields();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    mantView.showLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void handleClearFields() {
        mantView.clearFields();
    }

    private void handleRowSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = mantView.getMantTable().getSelectedRow();
            if (row >= 0) {
                MantResponseDto mant = mantView.getTableModel().getMants().get(row);
                mantView.populateFields(mant);
            }
        }
    }
}
