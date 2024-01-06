package cl.nvh.estudio.compras.data.modelo

import java.io.Serializable

class Compra (
    val id:String,
    val descripcion:String,
    var seleccionado: Boolean
) : Serializable {
}
