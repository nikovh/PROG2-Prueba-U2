package cl.nvh.estudio.compras.ui.state

import cl.nvh.estudio.compras.data.modelo.Compra

data class ComprasUIState (
    val mensaje:String = "",
    val compras:List<Compra> = listOf<Compra>()
)