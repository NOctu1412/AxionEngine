import dev.axion.structure.DeclaredField

data class TestStructure(
    @DeclaredField
    val name: String,
    @DeclaredField
    val age: Int,
    @DeclaredField
    val height: Float,
)