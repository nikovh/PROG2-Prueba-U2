package cl.nvh.estudio.compras.data

import android.util.Log
import cl.nvh.estudio.compras.data.modelo.Compra
import kotlinx.coroutines.flow.MutableStateFlow

class CompraMemoryDataSource {
    private val _compras = mutableListOf<Compra>()

    //parametros pag configuracion
    private val _Alfabeto = MutableStateFlow(false)
    private val _MostrarItems = MutableStateFlow(false)

    init {
        _compras.addAll(comprasDePrueba())
    }

    fun getMostrarItems(): Boolean{
        return _MostrarItems.value
    }

    fun setMostrarItems(seleccionadoBoolean: Boolean){
        _MostrarItems.value = seleccionadoBoolean
    }

    fun getAlfabeto(): Boolean{
        return _Alfabeto.value
    }

    fun setAlfabeto(seleccionadoBoolean: Boolean){
        _Alfabeto.value = seleccionadoBoolean
        }


    fun obtenerTodas():List<Compra> {
        return _compras
    }

    fun insertar(vararg compras: Compra) {
        _compras.addAll( compras.asList() )
    }

    fun eliminar(compra: Compra) {
        _compras.remove(compra)
        Log.v("DataSource", _compras.toString())
    }

    fun actualizarChecked(id: String, ischecked : Boolean){
        _compras.find { it.id == id }?.seleccionado = ischecked
        }

    private fun comprasDePrueba():List<Compra> = listOf()
}
