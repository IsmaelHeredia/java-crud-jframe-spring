// Written by Ismael Heredia in the year 2016

package sistema.ui;

import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sistema.dao.ProductoDAO;
import sistema.dao.ProveedorDAO;
import sistema.functions.Funciones;
import sistema.models.Producto;
import sistema.models.Proveedor;

public class FormProductos extends javax.swing.JFrame {

    /**
     * Creates new form FormProductos
     */
    ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
    private ArrayList lista_proveedores;
    private boolean nuevo;
    
    public FormProductos() {
        initComponents();
    }
    
    public void cargarListaProductos() {
        
        DefaultTableModel limpiar = (DefaultTableModel) lvProductos.getModel();
        while (limpiar.getRowCount() > 0) {
            limpiar.removeRow(0);
        }
        
        ProductoDAO productoDAO = (ProductoDAO) context.getBean("ProductoDAO");
        ArrayList listaProductos = productoDAO.listProductos(txtBuscar.getText());
        
        for (int i = 0; i < listaProductos.size(); i++) {
            
            Producto producto = (Producto) listaProductos.get(i);
            
            int id_producto = producto.getId_producto();
            String nombre_producto = producto.getNombre_producto();
            String descripcion = producto.getDescripcion();
            double precio = producto.getPrecio();
            String fecha_registro = producto.getFecha_registro();
            int id_proveedor = producto.getId_proveedor();
            
            String nombre_proveedor = productoDAO.cargarNombreProveedor(id_proveedor);
            
            DefaultTableModel modelo = (DefaultTableModel) lvProductos.getModel();
            modelo.addRow(new Object[]{id_producto, nombre_producto, descripcion, precio, fecha_registro, nombre_proveedor});
            
        }
        
    }
    
    public void cargarComboBoxProveedores() {
        
        ProveedorDAO proveedorDAO = (ProveedorDAO) context.getBean("ProveedorDAO");
        
        lista_proveedores = new ArrayList();
        ArrayList proveedores = proveedorDAO.listProveedores(txtBuscar.getText());
        
        DefaultComboBoxModel modelo1 = new DefaultComboBoxModel();
        
        for (int i = 0; i < proveedores.size(); i++) {
            Proveedor proveedor = (Proveedor) proveedores.get(i);
            String nombre_empresa = proveedor.getNombre_empresa();
            modelo1.addElement(nombre_empresa);
            lista_proveedores.add(proveedor);
        }
        
        cmbProveedor.setModel(modelo1);
        
    }
    
    public void cargarCamposProducto(int id_producto_to_load) {
        
        ProductoDAO productoDAO = (ProductoDAO) context.getBean("ProductoDAO");
        Producto producto = productoDAO.findByProductoId(id_producto_to_load);
        
        int id_producto = producto.getId_producto();
        String nombre_producto = producto.getNombre_producto();
        String descripcion = producto.getDescripcion();
        double precio = producto.getPrecio();
        int id_proveedor = producto.getId_proveedor();
        
        String nombre_proveedor = productoDAO.cargarNombreProveedor(id_proveedor);
        
        txtID.setText(Integer.toString(id_producto));
        txtNombre.setText(nombre_producto);
        taDescripcion.setText(descripcion);
        txtPrecio.setText(Double.toString(precio));
        
        cmbProveedor.setSelectedItem(nombre_proveedor);

        //JOptionPane.showMessageDialog(this, "Cargado : " + id_producto_to_load);
    }
    
    public void limpiar() {
        txtID.setText("");
        txtNombre.setText("");
        taDescripcion.setText("");
        txtPrecio.setText("");
        cmbProveedor.setSelectedIndex(0);
    }
    
    public boolean validar() {
        boolean respuesta = false;
        Funciones funcion = new Funciones();
        if ("".equals(txtNombre.getText())) {
            txtNombre.grabFocus();
            respuesta = false;
            JOptionPane.showMessageDialog(this, "Falta el nombre");
        } else if ("".equals(taDescripcion.getText())) {
            taDescripcion.grabFocus();
            respuesta = false;
            JOptionPane.showMessageDialog(this, "Falta la descripcion");
        } else if (cmbProveedor.getSelectedIndex() == -1) {
            cmbProveedor.grabFocus();
            respuesta = false;
            JOptionPane.showMessageDialog(this, "Seleccione proveedor");
        } else if (txtPrecio.getText() == "" || !funcion.valid_number(txtPrecio.getText())) {
            txtPrecio.grabFocus();
            respuesta = false;
            JOptionPane.showMessageDialog(this, "Falta el precio");
        } else {
            respuesta = true;
        }
        return respuesta;
    }
    
    public void agregar() {
        status.setText("[+] Programa en modo nuevo");
        nuevo = true;
        limpiar();
        //txtID.setText(String.valueOf(conexion_now.cargar_id_nuevo_para_producto()));
        JOptionPane.showMessageDialog(this, "Programa cargado en modo nuevo");
    }
    
    public void editar() {
        status.setText("[+] Programa en modo editar");
        nuevo = false;
        JOptionPane.showMessageDialog(this, "Programa cargado en modo editar");
    }
    
    public void borrar() {
        int row = lvProductos.getSelectedRow();
        int id_producto_to_load = (Integer) lvProductos.getValueAt(row, 0);
        if (row >= 0) {
            ProductoDAO productoDAO = (ProductoDAO) context.getBean("ProductoDAO");
            Producto producto = productoDAO.findByProductoId(id_producto_to_load);
            int id_producto = producto.getId_producto();
            String nombre_producto = producto.getNombre_producto();
            int resultado = JOptionPane.showConfirmDialog(this,
                    "¿ Desea borrar el producto " + nombre_producto + " ?",
                    "Borrar producto", JOptionPane.YES_NO_CANCEL_OPTION);
            if (resultado == JOptionPane.YES_OPTION) {
                
                String sql = "delete from productos where id_producto=" + id_producto;
                if (productoDAO.delete(producto)) {
                    status.setText("[+] Registro borrado");
                    JOptionPane.showMessageDialog(this, "Registro borrado");
                } else {
                    JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la base de datos");
                    status.setText("[-] Ha ocurrido un error en la base de datos");
                }
                cargarListaProductos();
                limpiar();
                
            }
        }
    }
    
    public void grabar() {
        Funciones funcion = new Funciones();
        if (validar()) {
            
            Producto p = new Producto();
            ProductoDAO productoDAO = (ProductoDAO) context.getBean("ProductoDAO");
            
            if (!"".equals(txtID.getText())) {
                p.setId_producto(Integer.parseInt(txtID.getText()));
            }
            p.setNombre_producto(txtNombre.getText());
            p.setDescripcion(taDescripcion.getText());
            p.setPrecio(Double.valueOf(txtPrecio.getText()));
            p.setFecha_registro(funcion.fecha_del_dia());
            
            int codigoProveedor = ((Proveedor) lista_proveedores.get(cmbProveedor.getSelectedIndex())).getId_proveedor();
            
            p.setId_proveedor(codigoProveedor);
            
            if (nuevo) {
                if (productoDAO.comprobar_existencia_producto_crear(p.getNombre_producto())) {
                    JOptionPane.showMessageDialog(this, "El producto " + p.getNombre_producto() + " ya existe");
                    status.setText("[-] El producto " + p.getNombre_producto() + " ya existe");
                } else {
                    if (productoDAO.insert(p)) {
                        JOptionPane.showMessageDialog(this, "Registro agregado");
                        status.setText("[+] Registro agregado");
                    } else {
                        JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la base de datos");
                        status.setText("[-] Ha ocurrido un error en la base de datos");
                    }
                }
            } else {
                if (productoDAO.comprobar_existencia_producto_editar(p.getId_producto(), p.getNombre_producto())) {
                    JOptionPane.showMessageDialog(this, "El producto " + p.getNombre_producto() + " ya existe");
                    status.setText("[-] El producto " + p.getNombre_producto() + " ya existe");
                } else {
                    if (productoDAO.update(p)) {
                        JOptionPane.showMessageDialog(this, "Registro actualizado");
                        status.setText("[+] Registro actualizado");
                    } else {
                        JOptionPane.showMessageDialog(this, "Ha ocurrido un error en la base de datos");
                        status.setText("[-] Ha ocurrido un error en la base de datos");
                    }
                }
            }
            
            limpiar();
            cargarListaProductos();
            
        }
    }
    
    public void cancelar() {
        status.setText("[+] Programa cargado");
        nuevo = false;
        limpiar();
        JOptionPane.showMessageDialog(this, "Opcion cancelada");
    }
    
    public void recargarLista() {
        cargarListaProductos();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpProducto = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblDescripcion = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDescripcion = new javax.swing.JTextArea();
        lblProveedor = new javax.swing.JLabel();
        cmbProveedor = new javax.swing.JComboBox();
        lblPrecio = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        btnGrabar = new javax.swing.JButton();
        txtID = new javax.swing.JTextField();
        jpProductos = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lvProductos = new javax.swing.JTable();
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
        setTitle("Productos");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jpProducto.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Agregar producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        lblNombre.setText("Nombre : ");

        lblDescripcion.setText("Descripcion");

        taDescripcion.setColumns(20);
        taDescripcion.setRows(5);
        jScrollPane1.setViewportView(taDescripcion);

        lblProveedor.setText("Proveedor : ");

        lblPrecio.setText("Precio : ");

        btnGrabar.setText("Grabar");
        btnGrabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGrabarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpProductoLayout = new javax.swing.GroupLayout(jpProducto);
        jpProducto.setLayout(jpProductoLayout);
        jpProductoLayout.setHorizontalGroup(
            jpProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jpProductoLayout.createSequentialGroup()
                        .addComponent(lblProveedor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbProveedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jpProductoLayout.createSequentialGroup()
                        .addGroup(jpProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpProductoLayout.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblDescripcion))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jpProductoLayout.createSequentialGroup()
                        .addGroup(jpProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblPrecio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtID))
                        .addGroup(jpProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpProductoLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(txtPrecio))
                            .addGroup(jpProductoLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnGrabar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jpProductoLayout.setVerticalGroup(
            jpProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblDescripcion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jpProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProveedor)
                    .addComponent(cmbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrecio)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGrabar)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpProductos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Productos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        lvProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre", "Descripcion", "Precio", "Fecha", "Proveedor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        lvProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lvProductosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lvProductos);

        lblNombreBuscar.setText("Nombre : ");

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpProductosLayout = new javax.swing.GroupLayout(jpProductos);
        jpProductos.setLayout(jpProductosLayout);
        jpProductosLayout.setHorizontalGroup(
            jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProductosLayout.createSequentialGroup()
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpProductosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jpProductosLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(lblNombreBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpProductosLayout.setVerticalGroup(
            jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpProductosLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombreBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jMenuItem1.setText("Agregar Producto");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Editar Producto");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Eliminar Producto");
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
                .addComponent(jpProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        agregar();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        cancelar();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void lvProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lvProductosMouseClicked
        int row = lvProductos.getSelectedRow();
        int id_producto = (Integer) lvProductos.getValueAt(row, 0);
        if (row >= 0) {
            cargarCamposProducto(id_producto);
        }
    }//GEN-LAST:event_lvProductosMouseClicked

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        editar();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        borrar();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        cargarListaProductos();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        grabar();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void btnGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGrabarActionPerformed
        grabar();
    }//GEN-LAST:event_btnGrabarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        lvProductos.getColumnModel().getColumn(0).setMaxWidth(0);
        lvProductos.getColumnModel().getColumn(0).setMinWidth(0);
        lvProductos.getColumnModel().getColumn(0).setPreferredWidth(0);
        txtID.setEditable(false);
        recargarLista();
        cargarComboBoxProveedores();
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
            java.util.logging.Logger.getLogger(FormProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormProductos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnGrabar;
    private javax.swing.JComboBox cmbProveedor;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel jpProducto;
    private javax.swing.JPanel jpProductos;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNombreBuscar;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblProveedor;
    private javax.swing.JTable lvProductos;
    private javax.swing.JLabel status;
    private javax.swing.JTextArea taDescripcion;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
