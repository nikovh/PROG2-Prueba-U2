package cl.nvh.estudio.compras

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.nvh.estudio.compras.data.modelo.Compra
import cl.nvh.estudio.compras.ui.vm.ComprasViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val comprasVm: ComprasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("MainActivity::onCreate", "Recuperando compras en disco")
        try {
            comprasVm.obtenerComprasGuardadasEnDisco(openFileInput(ComprasViewModel.FILE_NAME))
        } catch (e: Exception) {
            Log.e("MainActivity::onCreate", "Archivo con compras no existe!!")
        }

        setContent {
            AppCompras(comprasVm)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.v("MainActivity::onPause", "Guardando a disco")
        comprasVm.guardarComprasEnDisco(openFileOutput(ComprasViewModel.FILE_NAME, MODE_PRIVATE))
    }

    override fun onStop() {
        super.onStop()
        Log.v("MainActivity::onStop", "Guardando a disco")

    }
}

@Composable
fun AppCompras(
    vm: ComprasViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomePageUI(vm,
                onButtonSettingsClicked = {
                    navController.navigate("settings")
                }
            )
        }
        composable("settings") {
            SettingsPageUI(vm,
                onBackButtonClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppComprasTopBar(
    title: String = "",
    showSettingsButton: Boolean = true,
    onButtonSettingsClicked: () -> Unit = {},
    showBackButton: Boolean = false,
    onBackButtonClicked: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = {
                    onBackButtonClicked()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás"
                    )
                }
            }
        },
        actions = {
            if (showSettingsButton) {
                IconButton(onClick = {
                    onButtonSettingsClicked()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Configuración"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}
@Preview(showSystemUi = true)
@Composable
fun SettingsPageUI(
    comprasVm: ComprasViewModel = viewModel(),
    onBackButtonClicked: () -> Unit = {}
) {
    val contexto = LocalContext.current

    var seDebeOrdenarAlfabeticamente by rememberSaveable {
        mutableStateOf(comprasVm.getAlfabeto())
    }

    var mostrarPrimero by rememberSaveable {
        mutableStateOf(comprasVm.getMostrarItems())
    }

    Scaffold(
        topBar = {
            AppComprasTopBar(
                title = contexto.getString(R.string.Config),
                showSettingsButton = false,
                showBackButton = true,
                onBackButtonClicked = onBackButtonClicked
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 10.dp,
                    vertical = it.calculateTopPadding()
                )
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(contexto.getString(R.string.Ordenar))

                seDebeOrdenarAlfabeticamente?.let { it1 ->
                    Switch(
                        checked = it1,
                        onCheckedChange = {
                            seDebeOrdenarAlfabeticamente = it
                            comprasVm.setAlfabeto(it)
                        })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(contexto.getString(R.string.Mostrar_primero))

                mostrarPrimero?.let { it2 ->
                    Switch(
                        checked = it2,
                        onCheckedChange = {
                            mostrarPrimero = it
                            comprasVm.setMostrarItems(it)
                        })
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomePageUI(
    comprasVm: ComprasViewModel = viewModel(),
    onButtonSettingsClicked: () -> Unit = {}
) {
    val contexto = LocalContext.current
    val textoLogo = contexto.getString(R.string.logo)
    val uiState by comprasVm.uiState.collectAsStateWithLifecycle()
    var mostrarMensaje by rememberSaveable {
        mutableStateOf(false)
    }
    var primeraEjecucion by rememberSaveable {
        mutableStateOf(true)
    }

    LaunchedEffect(uiState.mensaje) {
        if (!primeraEjecucion) {
            mostrarMensaje = true
            delay(2_000)
            mostrarMensaje = false
        }
        primeraEjecucion = false
    }

    Scaffold(
        topBar = {
            AppComprasTopBar(
                title = contexto.getString(R.string.app_name),
                onButtonSettingsClicked = onButtonSettingsClicked
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = mostrarMensaje,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = uiState.mensaje,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(10.dp)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = textoLogo,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(150.dp)
                    .padding(10.dp)
            )
            ComprasFormUI {
                comprasVm.agregarCompra(it)
            }
            Spacer(modifier = Modifier.height(20.dp))
            ComprasListaUI(
                compras = uiState.compras,
                onDelete = {
                    comprasVm.eliminarCompra(it)
                },
                checked = { c, t ->
                    // check.value = isChe
                    comprasVm.actualizarCompra(c, t)
                },
                comprasVm.getAlfabeto(),
                comprasVm.getMostrarItems()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprasFormUI(
    onClickAgregarCompra: (compra: String) -> Unit
) {
    val contexto = LocalContext.current
    val textTaskPlaceholder = contexto.getString(R.string.compras_form_ejemplo)
    val textButtonAddTask = contexto.getString(R.string.compras_form_agregar)

    val (descripcionCompras, setDescripcionCompras) = rememberSaveable {
        mutableStateOf("")
    }
    Box(
        contentAlignment = Alignment.CenterEnd, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        TextField(
            value = descripcionCompras,
            onValueChange = {
                setDescripcionCompras(it)
            },
            placeholder = { Text(textTaskPlaceholder) },
            modifier = Modifier.fillMaxWidth()
        )
        TooltipBox(
            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
            tooltip = { PlainTooltip { Text(textButtonAddTask) } },
            state = rememberTooltipState()
        ) {
            IconButton(onClick = {
                Log.v("ComprasFormUI::IconButton", "Agregar Compra")
                onClickAgregarCompra(descripcionCompras)
                setDescripcionCompras("")
            }) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = textButtonAddTask,
                    modifier = Modifier
                        .size(30.dp)
                )
            }
        }
    }
}

@Composable
fun ComprasListaUI(
    compras: List<Compra>,
    onDelete: (c: Compra) -> Unit,
    checked: (c: Compra, t: Boolean) -> Unit,
    orderAlfa: Boolean?,
    orderMostrar: Boolean?
) {
    var listacompras = compras
    if (orderAlfa == true) {
        listacompras = compras.sortedBy { it.descripcion }
    }
    if (orderMostrar == true) {
        listacompras = compras.sortedBy { it.descripcion }.sortedBy { it.seleccionado }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(listacompras) {
            ComprasListaItemUI(it, onDelete, checked)
        }
    }
}

@Composable
fun ComprasListaItemUI(
    compra: Compra,
    onDelete: (c: Compra) -> Unit,
    checked: (c: Compra, t: Boolean) -> Unit
) {
    val contexto = LocalContext.current
    val textoEliminarCompra = contexto.getString(R.string.compras_form_eliminar)

    var checkeado = mutableStateOf(compra.seleccionado)


    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!compra.seleccionado) {
                Text(
                    text = compra.descripcion,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(2.0f)
                        .padding(10.dp, 8.dp)
                )
            } else {
                Text(
                    text = compra.descripcion,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(2.0f)
                        .padding(10.dp, 8.dp),
                    textDecoration = TextDecoration.LineThrough
                )
            }
            Checkbox(
                checked = checkeado.value,
                onCheckedChange = {
                    checked(compra, it)
                    checkeado.value = it
                })
            IconButton(onClick = {
                Log.v("TareaListaItemUI::IconButton", "onClick DELETE")
                onDelete(compra)
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = textoEliminarCompra,
                    tint = Color.Black,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        HorizontalDivider()
    }
}
