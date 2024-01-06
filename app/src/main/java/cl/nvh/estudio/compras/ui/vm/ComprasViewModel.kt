package cl.nvh.estudio.compras.ui.vm

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.nvh.estudio.compras.data.ComprasRepository
import cl.nvh.estudio.compras.data.modelo.Compra
import cl.nvh.estudio.compras.ui.state.ComprasUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

class ComprasViewModel (
    private val comprasRepository:ComprasRepository = ComprasRepository()
) : ViewModel() {

    companion object {
        const val FILE_NAME = "compras.data"
    }

    private var job: Job? = null

    // deja la versión mutable como privada
    private val _uiState = MutableStateFlow(ComprasUIState())
    // y pública la versión de solo lectura
    // así se asegura que sólo el ViewModel pueda modificar
    val uiState: StateFlow<ComprasUIState> = _uiState.asStateFlow()

    fun getAlfabeto(): Boolean {
        return comprasRepository.getAlfabetoEnDisco()
    }

    fun setAlfabeto(orderAlfa: Boolean) {
        comprasRepository.setAlfabetoEnDisco(orderAlfa)
    }

    fun getMostrarItems(): Boolean {
        return comprasRepository.getMostrarItemsEnDisco()
    }

    fun setMostrarItems(orderAlfa: Boolean) {
        comprasRepository.setMostrarItemsEnDisco(orderAlfa)
        }

    init {
        obtenerCompras()
    }

    fun obtenerComprasGuardadasEnDisco(fileInputStream: FileInputStream) {
        comprasRepository.getComprasEnDisco(fileInputStream)
    }

    fun guardarComprasEnDisco(fileOutputStream: FileOutputStream) {
        comprasRepository.guardarComprasEnDisco(fileOutputStream)
    }

    private fun obtenerCompras() {
        job?.cancel()
        job = viewModelScope.launch {
            val comprasStream = comprasRepository.getComprasStream()
            comprasStream.collect { comprasActualizadas ->
                Log.v("ComprasViewModel", "obtenerCompras() update{}")
                _uiState.update { currentState ->
                    currentState.copy(
                        compras = comprasActualizadas
                    )
                }
            }
        }
    }

    fun agregarCompra(compra: String) {
        if (!compra.isNullOrBlank()) {
                job = viewModelScope.launch {
                    val c = Compra(UUID.randomUUID().toString(), compra, false)
                    comprasRepository.insertar(c)
                    _uiState.update {
                        it.copy(mensaje = "Compra agregada: ${c.descripcion}")
                    }
                    obtenerCompras()
                }
            }
    }

    fun eliminarCompra(compra:Compra) {
        job = viewModelScope.launch {
            comprasRepository.eliminar(compra)
            _uiState.update {
                it.copy(mensaje = "Compra eliminada: ${compra.descripcion}")
            }
            obtenerCompras()
        }
    }

    fun actualizarCompra(compra: Compra , isCheked: Boolean) {
        job = viewModelScope.launch {
            comprasRepository.actualizarCheckedComprasEnDisco(compra.id, isCheked)
            _uiState.update {
                it.copy(mensaje = "Compra agregada: ${compra.descripcion}")
            }
            obtenerCompras()
            }
        }
}