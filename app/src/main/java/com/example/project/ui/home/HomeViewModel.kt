package com.example.project.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project.ConstructionProject
import com.example.project.ConstructionService
import com.example.project.R

class HomeViewModel : ViewModel() {
    private val _projects = MutableLiveData<List<ConstructionProject>>().apply {
        value = listOf(
            ConstructionProject(
                id = 1,
                title = "ЖК 'Смольный парк'",
                imageRes = R.drawable.smolny,
                progress = 65,
                address = "ул. Смольного, 15"
            ),
            ConstructionProject(
                id = 2,
                title = "ЖК 'Grand House'",
                imageRes = R.drawable.grandhouse,
                progress = 42,
                address = "ул. Миргородская, 20"
            )
        )
    }
    val projects: LiveData<List<ConstructionProject>> = _projects

    private val _services = MutableLiveData<List<ConstructionService>>().apply {
        value = listOf(
            ConstructionService(
                id = 1,
                name = "Проектирование",
                iconRes = android.R.drawable.ic_menu_edit,
                description = "Полный комплекс проектных работ"
            ),
            ConstructionService(
                id = 2,
                name = "Строительство",
                iconRes = android.R.drawable.ic_menu_edit,
                description = "Возведение зданий под ключ"
            )
        )
    }
    val services: LiveData<List<ConstructionService>> = _services
}