package com.syed.jetpacktwo.domain.model

data class ExpectedItem(
    val id: String,
    val epc: String,
    val description: String,
    val department: String
)

data class ProductProgress(
    val description: String,
    val expectedCount: Int,
    val scannedCount: Int
) {
    val isComplete: Boolean
        get() = scannedCount >= expectedCount && expectedCount > 0
}

data class DepartmentProgress(
    val department: String,
    val products: List<ProductProgress>
) {
    val totalExpected: Int
        get() = products.sumOf { it.expectedCount }
        
    val totalScanned: Int
        get() = products.sumOf { it.scannedCount }
        
    val isComplete: Boolean
        get() = totalScanned >= totalExpected && totalExpected > 0
}

// object ExpectedInventory {
//     val items = listOf(
//         ExpectedItem("9999000408", "C00000000000009999000408", "Shoes Rack", "Common"),
//         ExpectedItem("9999000413", "C00000000000009999000413", "Glass Top Stand (L)", "Womens"),
//         ExpectedItem("9999000420", "C00000000000009999000420", "Standalone Stand (Steel)", "Womens"),
//         ExpectedItem("9999000427", "C00000000000009999000427", "Glass Top Stand (R)", "Womens"),
//         ExpectedItem("9999000429", "C00000000000009999000429", "Wall Rack Left Top (Corner)", "Mens"),
//         ExpectedItem("9999000432", "C00000000000009999000432", "Wall Rack Left Bottom (Corner)", "Mens"),
//         ExpectedItem("9999000434", "C00000000000009999000434", "Wall Rack Right Top (Corner)", "Mens"),
//         ExpectedItem("9999000441", "C00000000000009999000441", "Wall Rack Right Bottom (Corner)", "Mens"),
//         ExpectedItem("9999000447", "C00000000000009999000447", "Tie Rack Top", "Mens"),
//         ExpectedItem("9999000463", "C00000000000009999000463", "Tie Rack Bottom", "Mens"),
//         ExpectedItem("9999000465", "C00000000000009999000465", "White wall rack left top", "Womens"),
//         ExpectedItem("9999000475", "C00000000000009999000475", "White wall rack right top", "Womens"),
//         ExpectedItem("9999000426", "C00000000000009999000426", "White wall rack Middle", "Womens"),
//         ExpectedItem("9999000425", "C00000000000009999000425", "White wall rack Bottom", "Womens"),
//         ExpectedItem("9999000401", "C00000000000009999000401", "A", "Kids"),
//         ExpectedItem("9999000403", "C00000000000009999000403", "C", "Kids"),
//         ExpectedItem("9999000412", "C00000000000009999000412", "L", "Kids"),
//         ExpectedItem("9999000444", "C00000000000009999000444", "P", "Kids"),
//         ExpectedItem("9999000448", "C00000000000009999000448", "X", "Boys"),
//         ExpectedItem("9999000443", "C00000000000009999000443", "S", "Boys"),
//         ExpectedItem("9999000457", "C00000000000009999000457", "V", "Boys"),
//         ExpectedItem("9999000472", "C00000000000009999000472", "W", "Boys"),
//         ExpectedItem("9999000473", "C00000000000009999000473", "Reception Table", "Common"),
//         ExpectedItem("9999000474", "C00000000000009999000474", "Coffee Table", "Bedding")
//     )
// }
