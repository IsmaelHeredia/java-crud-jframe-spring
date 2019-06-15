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
import sistema.models.Usuario;

public class JdbcUsuarioDAO implements UsuarioDAO {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean ingreso_usuario(String username, String password) {
        boolean respuesta = false;
        String sql = "select id_usuario from usuarios where usuario='" + username + "' and clave='" + password + "'";
        Connection conn = null;
        int cantidad = 0;
        try {
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
            conn.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return respuesta;
    }

    public boolean insert(Usuario usuario) {
        boolean respuesta = false;
        String sql = "insert into usuarios(usuario,clave,tipo,fecha_registro) values(?,?,?,?);";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getPassword());
            ps.setInt(3, usuario.getTipo());
            ps.setString(4, usuario.getFecha_registro());
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

    public boolean update(Usuario usuario) {
        boolean respuesta = false;
        String sql = "update usuarios set tipo=? where id_usuario=?";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, usuario.getTipo());
            ps.setInt(2, usuario.getId_usuario());
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

    public boolean cambiar_usuario(String usuario, String nuevo_usuario) {
        boolean respuesta = false;
        String sql = "update usuarios set usuario=? where usuario=?";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nuevo_usuario);
            ps.setString(2, usuario);
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

    public boolean cambiar_password(String usuario, String nuevo_password) {
        boolean respuesta = false;
        String sql = "update usuarios set clave=? where usuario=?";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nuevo_password);
            ps.setString(2, usuario);
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

    public boolean delete(Usuario usuario) {
        boolean respuesta = false;
        String sql = "delete from usuarios where id_usuario=" + usuario.getId_usuario();
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

    public Usuario findByUsuarioId(int id_usuario_to_load) {
        Connection conn = null;
        Usuario usuario = new Usuario();
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            String sql = "select id_usuario,usuario,clave,tipo,fecha_registro from usuarios where id_usuario=" + id_usuario_to_load;

            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                int id_usuario_load = rs.getInt(1);
                String nombre = rs.getString(2);
                String password = rs.getString(3);
                int tipo = rs.getInt(4);
                String fecha_registro = rs.getString(5);
                usuario.setId_usuario(id_usuario_load);
                usuario.setNombre(nombre);
                usuario.setPassword(password);
                usuario.setTipo(tipo);
                usuario.setFecha_registro(fecha_registro);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return usuario;
    }

    public ArrayList listUsuarios(String patron) {
        Connection conn = null;
        ArrayList listaUsuarios = new ArrayList();
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            String sql = "select id_usuario,usuario,clave,tipo,fecha_registro from usuarios";

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                int id_usuario = rs.getInt(1);
                String nombre = rs.getString(2);
                String password = rs.getString(3);
                int tipo = rs.getInt(4);
                String fecha_registro = rs.getString(5);
                Pattern regex1 = null;
                Matcher regex2 = null;
                regex1 = Pattern.compile(patron);
                regex2 = regex1.matcher(nombre);
                if (regex2.find()) {
                    Usuario u = new Usuario(id_usuario, nombre, password, tipo, fecha_registro);
                    listaUsuarios.add(u);
                }
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return listaUsuarios;
    }

    public boolean es_admin(String username) {
        Connection conn = null;
        boolean respuesta = false;
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            String sql = "select tipo from usuarios where usuario='" + username + "'";

            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                int tipo = rs.getInt(1);
                if (tipo == 1) {
                    respuesta = true;
                } else {
                    respuesta = false;
                }
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return respuesta;
    }

    public boolean comprobar_existencia_usuario_crear(String nombre_usuario) {
        boolean respuesta = false;
        try {
            Connection conn = null;
            String sql = "select * from usuarios where usuario like '" + nombre_usuario + "'";
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

    public boolean comprobar_existencia_usuario_editar(String nombre_usuario) {
        boolean respuesta = false;
        try {
            String sql = "select * from usuarios where usuario like '" + nombre_usuario + "'";
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
