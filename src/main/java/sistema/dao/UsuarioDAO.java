// Written by Ismael Heredia in the year 2016

package sistema.dao;

import java.util.ArrayList;
import sistema.models.Usuario;

public interface UsuarioDAO {

    public boolean insert(Usuario usuario);

    public boolean update(Usuario usuario);

    public boolean delete(Usuario usuario);

    public Usuario findByUsuarioId(int id_producto_to_load);

    public ArrayList listUsuarios(String patron);

    public boolean ingreso_usuario(String username, String password);

    public boolean es_admin(String username);

    public boolean comprobar_existencia_usuario_crear(String nombre_usuario);

    public boolean comprobar_existencia_usuario_editar(String nombre_usuario);
    
    public boolean cambiar_usuario(String usuario, String nuevo_usuario);
    
    public boolean cambiar_password(String usuario, String nuevo_password);

}
