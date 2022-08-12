data class HeartbeatsJSON(
    val user: User,
    val range: Range,
    val days: List<HeartbeatDay>,
)
