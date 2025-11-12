package dao;

import java.util.List;

public interface iDao<T> {
    T guardar(T t);
    T buscar(Integer id);
    void eliminar(Integer id);
    void actualizar(T t);
    T buscarGenerico(String parametro);
    List<T> buscarTodos();
}
