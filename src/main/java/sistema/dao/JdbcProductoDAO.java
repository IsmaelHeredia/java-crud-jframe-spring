// Written by Ismael Heredia in the year 2016

package sistema.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import sistema.dao.ProductoDAO;
import sistema.models.Producto;

public class JdbcProductoDAO implements ProductoDAO {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean insert(Producto producto) {
        boolean respuesta = false;
        String sql = "insert into productos(nombre_producto,descripcion,precio,id_proveedor,fecha_registro) values(?,?,?,?,?);";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, producto.getNombre_producto());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getId_proveedor());
            ps.setString(5, producto.getFecha_registro());
            int cantidad = ps.executeUpdate();
            if (cantidad > 0) {
                respuesta = true;
            } else {
                respuesta = false;
            }
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return respuesta;
    }

    public boolean update(Producto producto) {
        boolean respuesta = false;
        String sql = "update productos set nombre_producto=?,descripcion=?,precio=?,id_proveedor=? where id_producto=?";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, producto.getNombre_producto());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getId_proveedor());
            ps.setInt(5, producto.getId_producto());
            int cantidad = ps.executeUpdate();
            if (cantidad > 0) {
                respuesta = true;
            } else {
                respuesta = false;
            }
            ps.close();
            conn.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return respuesta;
    }

    public boolean delete(Producto producto) {
        boolean respuesta = false;
        String sql = "delete from productos where id_producto=" + producto.getId_producto();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            int cantidad = st.executeUpdate(sql);
            if (cantidad > 0) {
                respuesta = true;
            } else {
                respuesta = false;
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return respuesta;
    }

    public Producto findByProductoId(int id_producto_to_load) {
        Connection conn = null;
        Producto producto = new Producto();
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            String sql = "select id_producto,nombre_producto,descripcion,precio,id_proveedor,fecha_registro from productos where id_producto=" + id_producto_to_load;

            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                int id_producto = rs.getInt(1);
                String nombre_producto = rs.getString(2);
                String descripcion = rs.getString(3);
                double precio = rs.getDouble(4);
                int id_proveedor = rs.getInt(5);
                String fecha_registro = rs.getString(6);
                producto.setId_producto(id_producto);
                producto.setNombre_producto(nombre_producto);
                producto.setDescripcion(descripcion);
                producto.setPrecio(precio);
                producto.setId_proveedor(id_proveedor);
                producto.setFecha_registro(fecha_registro);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return producto;
    }

    public ArrayList listProductos(String patron) {
        Connection conn = null;
        ArrayList listaProductos = new ArrayList();
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            String sql = "select id_producto,nombre_producto,descripcion,precio,id_proveedor,fecha_registro from productos";

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                int id_producto = rs.getInt(1);
                String nombre_producto = rs.getString(2);
                String descripcion = rs.getString(3);
                double precio = rs.getDouble(4);
                int id_proveedor = rs.getInt(5);
                String fecha_registro = rs.getString(6);
                Pattern regex1 = null;
                Matcher regex2 = null;
                regex1 = Pattern.compile(patron);
                regex2 = regex1.matcher(nombre_producto);
                if (regex2.find()) {
                    Producto p = new Producto(id_producto, nombre_producto, descripcion, precio, id_proveedor, fecha_registro);
                    listaProductos.add(p);
                }
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return listaProductos;
    }

    public String cargarNombreProveedor(int id_proveedor) {
        Connection conn = null;
        String nombre_empresa = "";
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            String sql = "select nombre_empresa from proveedores where id_proveedor='" + id_proveedor + "'";

            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                nombre_empresa = rs.getString(1);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return nombre_empresa;
    }

    public boolean comprobar_existencia_producto_crear(String nombre_producto) {
        boolean respuesta = false;
        try {
            Connection conn = null;
            String sql = "select * from productos where nombre_producto like '" + nombre_producto + "'";
            int cantidad = 0;
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                cantidad++;
            }
            if (cantidad >= 1) {
                respuesta = true;
            } else {
                respuesta = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JdbcProveedorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return respuesta;
    }

    public boolean comprobar_existencia_producto_editar(int id_producto, String nombre_producto) {
        boolean respuesta = false;
        try {
            String sql = "select * from productos where nombre_producto like '" + nombre_producto + "' and id_producto!=" + id_producto;
            Connection conn = null;
            int cantidad = 0;
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                cantidad++;
            }
            if (cantidad >= 1) {
                respuesta = true;
            } else {
                respuesta = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JdbcProveedorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return respuesta;
    }

}
