data class SummaryDay(
        val categories: List<Category>,
        val date: String,
        val dependencies: List<Category>,
        val editors: List<Category>,
        val grand_total: GrandTotal,
        val languages: List<Category>,
        val machines: List<Machine>,
        val operating_systems: List<Category>,
        val projects: List<Project>
    )