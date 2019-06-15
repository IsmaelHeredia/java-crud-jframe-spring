// Written by Ismael Heredia in the year 2016

package sistema.ui;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sistema.dao.UsuarioDAO;
import sistema.functions.Funciones;
import sistema.models.Usuario;

public class FormUsuarios extends javax.swing.JFrame {

    /**
     * Creates new form FormUsuarios
     */
    ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
    boolean nuevo = false;
    
    public FormUsuarios() {
        initComponents();
    }
    
    public void cargarListaUsuarios() {
        
        DefaultTableModel limpiar = (DefaultTableModel) lvUsuarios.getModel();
        while (limpiar.getRowCount() > 0) {
            limpiar.removeRow(0);
        }
        
        UsuarioDAO usuarioDAO = (UsuarioDAO) context.getBean("UsuarioDAO");
        ArrayList listaUsuarios = usuarioDAO.listUsuarios(txtBuscar.getText());
        
        for (int i = 0; i < listaUsuarios.size(); i++) {
            
            Usuario usuario = (Usuario) listaUsuarios.get(i);
            
            int id_usuario = usuario.getId_usuario();
            String nombre = usuario.getNombre();
            String password = usuario.getPassword();
            int tipo = usuario.getTipo();
            String fecha_registro = usuario.getFecha_registro();
            
            String nombre_tipo = "";
            
            if (tipo == 1) {
                nombre_tipo = "Administrador";
            } else {
                nombre_tipo = "Usuario";
            }
            
            DefaultTableModel modelo = (DefaultTableModel) lvUsuarios.getModel();
            modelo.addRow(new Object[]{id_usuario, nombre_tipo, nombre, fecha_registro});
            
        }
        
    }
    
    public void limpiar() {
        txtID.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        cmbTipo.setSelectedIndex(-1);
    }
    
    public boolean validar() {
        boolean respuesta = false;
        
        if ("".equals(txtUsuario.getText())) {
            txtUsuario.grabFocus();
            respuesta = false;
            JOptionPane.showMessageDialog(this, "Falta el nombre de usuario");
        } else if ("".equals(txtPassword.getText()) && nuevo == true) {
            txtPassword.grabFocus();
            respuesta = false;
            JOptionPane.showMessageDialog(this, "Falta contraseña");
        } else if (cmbTipo.getSelectedIndex() == -1) {
            cmbTipo.grabFocus();
            respuesta = false;
            JOptionPane.showMessageDialog(this, "Seleccione tipo");
        } else {
            respuesta = true;
        }
        return respuesta;
    }
    
    public void agregar() {
        txtUsuario.setEditable(true);
        txtPassword.setEditable(true);
        status.setText("[+] Programa en modo nuevo");
        limpiar();
        nuevo = true;
        JOptionPane.showMessageDialog(this, "Programa cargado en modo nuevo");
    }
    
    public void editar() {
        txtUsuario.setEditable(false);
        txtPassword.setEditable(false);
        status.setText("[+] Programa en modo editar");
        nuevo = false;
        JOptionPane.showMessageDialog(this, "Programa cargado en modo editar");
    }
    
    public void asignar(String tipo_usuario) {
        int row = lvUsuarios.getSelectedRow();
        int id_usuario_to_load = (Integer) lvUsuarios.getValueAt(row, 0);
        if (row >= 0) {
            UsuarioDAO usuarioDAO = (UsuarioDAO) context.getBean("UsuarioDAO");
            Usuario usuario = usuarioDAO.findByUsuarioId(id_usuario_to_load);
            int id_usuario = usuario.getId_usuario();
            String nombre_usuario = usuario.getNombre();
            
            int tipo = 0;
            String rango = "";
            
            if ("admin".equals(tipo_usuario)) {
                tipo = 1;
                rango = "Administrador";
            } else if ("user".equals(tipo_usuario)) {
                tipo = 2;
                rango = "Usuario";
            } else {
                tipo = 2;
                rango = "Usuario";
            }
            
            usuario.setTipo(tipo);
            
            int resultado = JOptionPane.showConfirmDialog(this,
                    "¿ Desea asignar como " + rango + " al usuario " + nombre_usuario + " ?",
                    "Asignar", JOptionPane.YES_NO_CANCEL_OPTION);
            if (resultado == JOptionPane.YES_OPTION) {
                
                if (usuarioDAO.update(usuario)) {
                    status.setText("[+] Asignacion realizada");
                    JOptionPane.showMessageDialog(this, "Asignacion realizada");
                } else {
                    JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la base de datos");
                    status.setText("[-] Ha ocurrido un error en la base de datos");
                }
                
                cargarListaUsuarios();
                limpiar();
            }
        }
    }
    
    public void borrar() {
        int row = lvUsuarios.getSelectedRow();
        int id_usuario_to_load = (Integer) lvUsuarios.getValueAt(row, 0);
        if (row >= 0) {
            UsuarioDAO usuarioDAO = (UsuarioDAO) context.getBean("UsuarioDAO");
            Usuario usuario = usuarioDAO.findByUsuarioId(id_usuario_to_load);
            int id_usuario = usuario.getId_usuario();
            String nombre_usuario = usuario.getNombre();
            int resultado = JOptionPane.showConfirmDialog(this,
                    "¿ Desea borrar al usuario " + nombre_usuario + " ?",
                    "Borrar usuario", JOptionPane.YES_NO_CANCEL_OPTION);
            if (resultado == JOptionPane.YES_OPTION) {
                
                if (usuarioDAO.delete(usuario)) {
                    status.setText("[+] Registro borrado");
                    JOptionPane.showMessageDialog(this, "Registro borrado");
                } else {
                    JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la base de datos");
                    status.setText("[-] Ha ocurrido un error en la base de datos");
                }
                
                cargarListaUsuarios();
                limpiar();
            }
        }
    }
    
    public void grabar() {
        Funciones funcion = new Funciones();
        if (validar()) {
            
            UsuarioDAO usuarioDAO = (UsuarioDAO) context.getBean("UsuarioDAO");
            Usuario usuario = new Usuario();
            Funciones funciones = new Funciones();
            
            if (!"".equals(txtID.getText())) {
                usuario.setId_usuario(Integer.parseInt(txtID.getText()));
            }
            
            String nombre = txtUsuario.getText();
            String password = funciones.md5_encode(txtPassword.getText());
            int tipo = 0;
            if (cmbTipo.getSelectedIndex() == 1) {
                tipo = 1;
            } else {
                tipo = 2;
            }
            String fecha_registro = funcion.fecha_del_dia();
            
            usuario.setNombre(nombre);
            usuario.setPassword(password);
            usuario.setTipo(tipo);
            usuario.setFecha_registro(fecha_registro);
            
            if (nuevo) {
                if (usuarioDAO.comprobar_existencia_usuario_crear(usuario.getNombre())) {
                    JOptionPane.showMessageDialog(this, "El usuario " + usuario.getNombre() + " ya existe");
                    status.setText("[-] El usuario " + usuario.getNombre() + " ya existe");
                } else {
                    if (usuarioDAO.insert(usuario)) {
                        JOptionPane.showMessageDialog(this, "Registro agregado");
                        status.setText("[+] Registro agregado");
                    } else {
                        JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la base de datos");
                        status.setText("[-] Ha ocurrido un error en la base de datos");
                    }
                }
            } else {
                if (usuarioDAO.update(usuario)) {
                    JOptionPane.showMessageDialog(this, "Registro actualizado");
                    status.setText("[+] Registro actualizado");
                } else {
                    JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la base de datos");
                    status.setText("[-] Ha ocurrido un error en la base de datos");
                }
            }
            
            limpiar();
            cargarListaUsuarios();
            
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpAgregarUsuario = new javax.swing.JPanel();
        lblUsuario = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        lblTipo = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        cmbTipo = new javax.swing.JComboBox();
        btnAgregar = new javax.swing.JButton();
        txtID = new javax.swing.JTextField();
        jpUsuarios = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lvUsuarios = new javax.swing.JTable();
        lblNombreBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        status = new javax.swing.JLabel();
        mbOpciones = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Usuarios");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jpAgregarUsuario.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Agregar Usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        lblUsuario.setText("Usuario : ");

        lblPassword.setText("Password : ");

        lblTipo.setText("Tipo : ");

        cmbTipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Usuario", "Administrador" }));

        btnAgregar.setText("Grabar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpAgregarUsuarioLayout = new javax.swing.GroupLayout(jpAgregarUsuario);
        jpAgregarUsuario.setLayout(jpAgregarUsuarioLayout);
        jpAgregarUsuarioLayout.setHorizontalGroup(
            jpAgregarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpAgregarUsuarioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpAgregarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpAgregarUsuarioLayout.createSequentialGroup()
                        .addComponent(lblUsuario)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUsuario))
                    .addGroup(jpAgregarUsuarioLayout.createSequentialGroup()
                        .addComponent(lblPassword)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPassword))
                    .addGroup(jpAgregarUsuarioLayout.createSequentialGroup()
                        .addComponent(lblTipo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbTipo, 0, 149, Short.MAX_VALUE))
                    .addGroup(jpAgregarUsuarioLayout.createSequentialGroup()
                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpAgregarUsuarioLayout.setVerticalGroup(
            jpAgregarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpAgregarUsuarioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpAgregarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUsuario)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpAgregarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpAgregarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTipo)
                    .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jpAgregarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpAgregarUsuarioLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(btnAgregar)
                        .addContainerGap(27, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpAgregarUsuarioLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        jpUsuarios.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Usuarios", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        lvUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Tipo", "Nombre", "Fecha Registro"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        lvUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lvUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lvUsuarios);

        lblNombreBuscar.setText("Nombre : ");

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpUsuariosLayout = new javax.swing.GroupLayout(jpUsuarios);
        jpUsuarios.setLayout(jpUsuariosLayout);
        jpUsuariosLayout.setHorizontalGroup(
            jpUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpUsuariosLayout.createSequentialGroup()
                .addGroup(jpUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jpUsuariosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpUsuariosLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(lblNombreBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpUsuariosLayout.setVerticalGroup(
            jpUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpUsuariosLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jpUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombreBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(status))
        );

        jMenu1.setText("Opciones");

        jMenuItem5.setText("Agregar usuario");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem6.setText("Editar Usuario");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenu2.setText("Cambiar tipo a ");

        jMenuItem1.setText("Usuario");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem2.setText("Administrador");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenu1.add(jMenu2);

        jMenuItem3.setText("Eliminar Usuario");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Recargar Lista");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        mbOpciones.add(jMenu1);

        setJMenuBar(mbOpciones);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jpAgregarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(14, 14, 14))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpAgregarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        borrar();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        agregar();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        cargarListaUsuarios();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        grabar();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        asignar("user");
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        asignar("admin");
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        lvUsuarios.getColumnModel().getColumn(0).setMaxWidth(0);
        lvUsuarios.getColumnModel().getColumn(0).setMinWidth(0);
        lvUsuarios.getColumnModel().getColumn(0).setPreferredWidth(0);
        txtID.setEditable(false);
        cargarListaUsuarios();
    }//GEN-LAST:event_formWindowOpened

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        cargarListaUsuarios();
    }//GEN-LAST:event_btnBuscarActionPerformed
    
    public void cargarCamposUsuario(int id_usuario_to_load) {
        
        UsuarioDAO usuarioDAO = (UsuarioDAO) context.getBean("UsuarioDAO");
        Usuario usuario = usuarioDAO.findByUsuarioId(id_usuario_to_load);
        int id_usuario = usuario.getId_usuario();
        String nombre_usuario = usuario.getNombre();
        int tipo = usuario.getTipo();
        
        String nombre_tipo = "";
        
        if (tipo == 1) {
            nombre_tipo = "Administrador";
        } else {
            nombre_tipo = "Usuario";
        }
        
        txtID.setText(Integer.toString(id_usuario));
        txtUsuario.setText(nombre_usuario);
        cmbTipo.setSelectedItem(nombre_tipo);
        
    }

    private void lvUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lvUsuariosMouseClicked
        int row = lvUsuarios.getSelectedRow();
        int id_usuario = (Integer) lvUsuarios.getValueAt(row, 0);
        if (row >= 0) {
            cargarCamposUsuario(id_usuario);
        }
    }//GEN-LAST:event_lvUsuariosMouseClicked

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        editar();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

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
            java.util.logging.Logger.getLogger(FormUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormUsuarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JComboBox cmbTipo;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jpAgregarUsuario;
    private javax.swing.JPanel jpUsuarios;
    private javax.swing.JLabel lblNombreBuscar;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JTable lvUsuarios;
    private javax.swing.JMenuBar mbOpciones;
    private javax.swing.JLabel status;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtID;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
