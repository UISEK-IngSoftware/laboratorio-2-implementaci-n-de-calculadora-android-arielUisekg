package ec.edu.uisek.calculator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
data class CalculatorState(
    val display: String = "0"
)
sealed class CalculatorEvent {
    data class Number(val number: String) : CalculatorEvent()
    data class Operator(val operator: String) : CalculatorEvent()
    object Clear : CalculatorEvent()
    object AllClear : CalculatorEvent()
    object Calculate : CalculatorEvent()
    object Decimal : CalculatorEvent()
}
class CalculatorViewModel: ViewModel(){
    private var number1: String = ""
    private var number2: String=""
    private var operator: String? = null
    var state by mutableStateOf(CalculatorState())
        private set
    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Number -> enterNumber(event.number)
            is CalculatorEvent.Operator -> enterOperator(event.operator)
            is CalculatorEvent.Decimal -> enterDecimal()
            is CalculatorEvent.AllClear -> clearAll()
            is CalculatorEvent.Clear -> clearLast() // Lo mejoramos para que sea más inteligente
            is CalculatorEvent.Calculate -> performCalculation()
        }
    }
    private fun enterNumber(number: String) {
        if (operator == null) {

            if (number1 == "0") {
                number1 = number
            } else {
                number1 += number
            }
            state = state.copy(display = number1)
        } else {
            if (number2 == "0") {
                number2 = number
            } else {
                number2 += number
            }
            state = state.copy(display = number2)
        }
    }


    private fun enterOperator(op: String) {
        if (number1.isNotBlank() && number2.isNotBlank()) {
            performCalculation()
        }
        if (number1.isNotBlank()) {
            operator = op

        }
    }

    private fun enterDecimal() {
        val currentNumber = if (operator==null) number1 else number2
        if(!currentNumber.contains(".")){
            if(operator == null){
                number1+="."
                state=state.copy(display = number1)
            } else {
                number2 +="."
                state=state.copy(display=number2)
            }
        }
    }
    private fun performCalculation() {
        val num1 = number1.toDoubleOrNull()
        val num2 = number2.toDoubleOrNull()

        if (num1 != null && num2 != null && operator != null) {
            val result = when (operator) {
                "+" -> num1 + num2
                "−" -> num1 - num2
                "×" -> num1 * num2
                "÷" -> if (num2 != 0.0) num1 / num2 else Double.NaN // Handle division by zero
                else -> 0.0 // Should not happen
            }

            val resultString = if (result.isNaN()) "Error" else result.toString().removeSuffix(".0")

            // Prepare for the next calculation
            number1 = resultString
            number2 = "" // Clear the second number
            operator = null // Clear the operator
            state = state.copy(display = resultString) // Display the result
        }
    }

    private fun clearLast() {
        if (operator == null) {
            if (number1.isNotBlank()) {
                number1 = number1.dropLast(1)
                state = state.copy(display = if (number1.isBlank()) "0" else number1)
            }
        } else {
            if (number2.isNotBlank()) {
                number2 = number2.dropLast(1)
                state = state.copy(display = if (number2.isBlank()) "0" else number2)
            } else {
                operator = null
                state = state.copy(display = number1)
            }
        }
    }
    private fun clearAll() {
        number1 = ""
        number2 = ""
        operator = null
        state = state.copy(display = "0")
    }
}