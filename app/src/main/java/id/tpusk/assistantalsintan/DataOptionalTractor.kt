package id.tpusk.assistantalsintan

class DataOptionalTractor(
    val name: String,
    val price: Double,
    val capacity: Double
) {
    val eff: Double = price / capacity
}