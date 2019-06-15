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
import sistema.models.Proveedor;

public class JdbcProveedorDAO implements ProveedorDAO {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean insert(Proveedor proveedor) {
        boolean respuesta = false;
        String sql = "insert into proveedores(nombre_empresa,direccion,telefono,fecha_registro_proveedor) values(?,?,?,?);";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, proveedor.getNombre_empresa());
            ps.setString(2, proveedor.getDireccion());
            ps.setInt(3, proveedor.getTelefono());
            ps.setString(4, proveedor.getFecha_registro());
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

    public boolean update(Proveedor proveedor) {
        boolean respuesta = false;
        String sql = "update proveedores set nombre_empresa=?,direccion=?,telefono=? where id_proveedor=?";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, proveedor.getNombre_empresa());
            ps.setString(2, proveedor.getDireccion());
            ps.setInt(3, proveedor.getTelefono());
            ps.setInt(4, proveedor.getId_proveedor());
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

    public boolean delete(Proveedor proveedor) {
        boolean respuesta = false;
        String sql = "delete from proveedores where id_proveedor=" + proveedor.getId_proveedor();
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

    public ArrayList listProveedores(String patron) {
        Connection conn = null;
        ArrayList listaProveedores = new ArrayList();
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            String sql = "select id_proveedor,nombre_empresa,direccion,telefono,fecha_registro_proveedor from proveedores";

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                int id_proveedor = rs.getInt(1);
                String nombre_empresa = rs.getString(2);
                String direccion = rs.getString(3);
                int telefono = rs.getInt(4);
                String fecha_registro = rs.getString(5);
                Pattern regex1 = null;
                Matcher regex2 = null;
                regex1 = Pattern.compile(patron);
                regex2 = regex1.matcher(nombre_empresa);
                if (regex2.find()) {
                    Proveedor p = new Proveedor(id_proveedor, nombre_empresa, direccion, telefono, fecha_registro);
                    listaProveedores.add(p);
                }
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return listaProveedores;
    }

    public Proveedor findByProveedorId(int id_proveedor_to_load) {
        Connection conn = null;
        Proveedor proveedor = new Proveedor();
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            String sql = "select id_proveedor,nombre_empresa,direccion,telefono,fecha_registro_proveedor from proveedores where id_proveedor=" + id_proveedor_to_load;

            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                int id_proveedor = rs.getInt(1);
                String nombre_empresa = rs.getString(2);
                String direccion = rs.getString(3);
                int telefono = rs.getInt(4);
                String fecha_registro = rs.getString(5);

                proveedor.setId_proveedor(id_proveedor);
                proveedor.setNombre_empresa(nombre_empresa);
                proveedor.setDireccion(direccion);
                proveedor.setTelefono(telefono);
                proveedor.setFecha_registro(fecha_registro);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return proveedor;
    }

    public boolean comprobar_existencia_proveedor_crear(String nombre_empresa) {
        boolean respuesta = false;
        try {
            Connection conn = null;
            String sql = "select * from proveedores where nombre_empresa like '" + nombre_empresa + "'";
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

    public boolean comprobar_existencia_proveedor_editar(int id_proveedor, String nombre_empresa) {
        boolean respuesta = false;
        try {
            String sql = "select * from proveedores where nombre_empresa like '" + nombre_empresa + "' and id_proveedor!=" + id_proveedor;
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
