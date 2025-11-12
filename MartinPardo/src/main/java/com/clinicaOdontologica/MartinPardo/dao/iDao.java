package com.clinicaOdontologica.MartinPardo.dao;

import java.util.List;

public interface iDao<T> {
    T guardar(T t);
    T buscar(Long id);
    void eliminar(Long id);
    void actualizar(T t);
    T buscarGenerico(String parametro);
    List<T> buscarTodos();
}
