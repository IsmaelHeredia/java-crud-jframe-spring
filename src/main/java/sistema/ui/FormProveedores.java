// Written by Ismael Heredia in the year 2016

package sistema.ui;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sistema.dao.ProveedorDAO;
import sistema.functions.Funciones;
import sistema.models.Proveedor;

public class FormProveedores extends javax.swing.JFrame {

    /**
     * Creates new form FormProveedores
     */
    public boolean nuevo;
    ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
    
    public FormProveedores() {
        initComponents();
    }
    
    public void cargarListaProveedores() {
        
        DefaultTableModel limpiar = (DefaultTableModel) lvProveedores.getModel();
        while (limpiar.getRowCount() > 0) {
            limpiar.removeRow(0);
        }
        
        ProveedorDAO proveedorDAO = (ProveedorDAO) context.getBean("ProveedorDAO");
        ArrayList listaProveedores = proveedorDAO.listProveedores(txtBuscar.getText());
        
        for (int i = 0; i < listaProveedores.size(); i++) {
            Proveedor proveedor = (Proveedor) listaProveedores.get(i);
            int id_proveedor = proveedor.getId_proveedor();
            String nombre_empresa = proveedor.getNombre_empresa();
            String direccion = proveedor.getDireccion();
            int telefono = proveedor.getTelefono();
            String fecha_registro = proveedor.getFecha_registro();
            
            DefaultTableModel modelo = (DefaultTableModel) lvProveedores.getModel();
            modelo.addRow(new Object[]{id_proveedor, nombre_empresa, direccion, telefono});
            
        }
        
    }
    
    public void cargarCamposProveedor(int id_proveedor_to_load) {
        
        ProveedorDAO proveedorDAO = (ProveedorDAO) context.getBean("ProveedorDAO");
        Proveedor proveedor = proveedorDAO.findByProveedorId(id_proveedor_to_load);
        int id_proveedor = proveedor.getId_proveedor();
        String nombre_empresa = proveedor.getNombre_empresa();
        String direccion = proveedor.getDireccion();
        int telefono = proveedor.getTelefono();
        
        txtID.setText(Integer.toString(id_proveedor));
        txtNombre.setText(nombre_empresa);
        txtDireccion.setText(direccion);
        txtTelefono.setText(Integer.toString(telefono));
        
    }
    
    public void limpiar() {
        txtID.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
    }
    
    public boolean validar() {
        boolean respuesta = false;
        Funciones funcion = new Funciones();
        if ("".equals(txtNombre.getText())) {
            txtNombre.grabFocus();
            respuesta = false;
            JOptionPane.showMessageDialog(this, "Falta el nombre");
        } else if ("".equals(txtDireccion.getText())) {
            txtDireccion.grabFocus();
            respuesta = false;
            JOptionPane.showMessageDialog(this, "Falta la direccion");
        } else if (txtTelefono.getText() == "" || !funcion.valid_number(txtTelefono.getText())) {
            txtTelefono.grabFocus();
            respuesta = false;
            JOptionPane.showMessageDialog(this, "Falta el telefono");
        } else {
            respuesta = true;
        }
        return respuesta;
    }
    
    public void agregar() {
        status.setText("[+] Programa en modo nuevo");
        nuevo = true;
        limpiar();
        //txtID.setText(String.valueOf(conexion_now.cargar_id_nuevo_para_proveedor()));
        JOptionPane.showMessageDialog(this, "Programa cargado en modo nuevo");
    }
    
    public void editar() {
        status.setText("[+] Programa en modo editar");
        nuevo = false;
        JOptionPane.showMessageDialog(this, "Programa cargado en modo editar");
    }
    
    public void borrar() {
        int row = lvProveedores.getSelectedRow();
        int id_proveedor_to_load = (Integer) lvProveedores.getValueAt(row, 0);
        if (row >= 0) {
            
            ProveedorDAO proveedorDAO = (ProveedorDAO) context.getBean("ProveedorDAO");
            Proveedor proveedor = proveedorDAO.findByProveedorId(id_proveedor_to_load);
            int id_proveedor = proveedor.getId_proveedor();
            String nombre_empresa = proveedor.getNombre_empresa();
            
            int resultado = JOptionPane.showConfirmDialog(this,
                    "¿ Desea borrar al proveedor " + nombre_empresa + " ?",
                    "Borrar proveedor", JOptionPane.YES_NO_CANCEL_OPTION);
            if (resultado == JOptionPane.YES_OPTION) {
                
                if (proveedorDAO.delete(proveedor)) {
                    status.setText("[+] Registro borrado");
                    JOptionPane.showMessageDialog(this, "Registro borrado");
                } else {
                    JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la base de datos");
                    status.setText("[-] Ha ocurrido un error en la base de datos");
                }
                
                cargarListaProveedores();
                limpiar();
                
            }
        }
    }
    
    public void grabar() {
        Funciones funcion = new Funciones();
        if (validar()) {
            
            Proveedor p = new Proveedor();
            ProveedorDAO proveedorDAO = (ProveedorDAO) context.getBean("ProveedorDAO");
            
            if (!"".equals(txtID.getText())) {
                p.setId_proveedor(Integer.parseInt(txtID.getText()));
            }
            p.setNombre_empresa(txtNombre.getText());
            p.setDireccion(txtDireccion.getText());
            p.setTelefono(Integer.parseInt(txtTelefono.getText()));
            p.setFecha_registro(funcion.fecha_del_dia());
                        
            //conexion_now.cargarConsulta(sql);
            if (nuevo) {
                if (proveedorDAO.comprobar_existencia_proveedor_crear(p.getNombre_empresa())) {
                    JOptionPane.showMessageDialog(this, "El proveedor " + p.getNombre_empresa() + " ya existe");
                    status.setText("[-] El proveedor " + p.getNombre_empresa() + " ya existe");
                } else {
                    if (proveedorDAO.insert(p)) {
                        JOptionPane.showMessageDialog(this, "Registro agregado");
                        status.setText("[+] Registro agregado");
                    } else {
                        JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la base de datos");
                        status.setText("[-] Ha ocurrido un error en la base de datos");
                    }
                }
            } else {
                if (proveedorDAO.comprobar_existencia_proveedor_editar(p.getId_proveedor(), p.getNombre_empresa())) {
                    JOptionPane.showMessageDialog(this, "El proveedor " + p.getNombre_empresa() + " ya existe");
                    status.setText("[-] El proveedor " + p.getNombre_empresa() + " ya existe");
                } else {
                    if (proveedorDAO.update(p)) {
                        JOptionPane.showMessageDialog(this, "Registro actualizado");
                        status.setText("[+] Registro actualizado");
                    } else {
                        JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la base de datos");
                        status.setText("[-] Ha ocurrido un error en la base de datos");
                    }
                }
            }
            
            limpiar();
            cargarListaProveedores();
            
        }
    }
    
    public void cancelar() {
        status.setText("[+] Programa cargado");
        nuevo = false;
        limpiar();
        JOptionPane.showMessageDialog(this, "Opcion cancelada");
    }
    
    public void recargarLista() {
        cargarListaProveedores();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        lblDireccion = new javax.swing.JLabel();
        lblTelefono = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        btnGrabar = new javax.swing.JButton();
        txtID = new javax.swing.JTextField();
        jpProveedores = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lvProveedores = new javax.swing.JTable();
        lblNombreBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        status = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Proveedores");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Proveedor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        lblNombre.setText("Nombre : ");

        lblDireccion.setText("Direccion :  ");

        lblTelefono.setText("Telefono : ");

        btnGrabar.setText("Grabar");
        btnGrabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGrabarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblNombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombre))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblDireccion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDireccion))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTelefono)
                            .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTelefono)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnGrabar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 51, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDireccion)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTelefono)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGrabar)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpProveedores.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Proveedores", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        lvProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre", "Direccion", "Telefono"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        lvProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lvProveedoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lvProveedores);

        lblNombreBuscar.setText("Nombre: ");

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpProveedoresLayout = new javax.swing.GroupLayout(jpProveedores);
        jpProveedores.setLayout(jpProveedoresLayout);
        jpProveedoresLayout.setHorizontalGroup(
            jpProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProveedoresLayout.createSequentialGroup()
                .addGroup(jpProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpProveedoresLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                    .addGroup(jpProveedoresLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(lblNombreBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpProveedoresLayout.setVerticalGroup(
            jpProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpProveedoresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombreBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        status.setText("Programa cargado");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(status)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(status)
        );

        jMenu1.setText("Opciones");

        jMenuItem1.setText("Agregar Proveedor");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Editar Proveedor");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Eliminar Proveedor");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Cancelar");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText("Recargar Lista");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem6.setText("Grabar");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lvProveedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lvProveedoresMouseClicked
        int row = lvProveedores.getSelectedRow();
        int id_proveedor = (Integer) lvProveedores.getValueAt(row, 0);
        if (row >= 0) {
            cargarCamposProveedor(id_proveedor);
        }
    }//GEN-LAST:event_lvProveedoresMouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        agregar();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void btnGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGrabarActionPerformed
        grabar();
    }//GEN-LAST:event_btnGrabarActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        grabar();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        borrar();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        editar();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        cancelar();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        cargarListaProveedores();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        lvProveedores.getColumnModel().getColumn(0).setMaxWidth(0);
        lvProveedores.getColumnModel().getColumn(0).setMinWidth(0);
        lvProveedores.getColumnModel().getColumn(0).setPreferredWidth(0);
        txtID.setEditable(false);
        recargarLista();
    }//GEN-LAST:event_formWindowOpened

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        recargarLista();
    }//GEN-LAST:event_btnBuscarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormProveedores().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnGrabar;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jpProveedores;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNombreBuscar;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JTable lvProveedores;
    private javax.swing.JLabel status;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
