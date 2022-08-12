data class Project(
        val branches: List<Category>,
        val categories: List<Category>,
        val dependencies: List<Category>,
        val editors: List<Category>,
        val entites: List<Entity>,
        val grand_total: GrandTotal,
        val languages: List<Category>,
        val machines: List<Machine>,
        val name: String?,
        val operating_systems: List<Category>,
    )