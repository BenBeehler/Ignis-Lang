# Ignis-Lang
<br>
## Contribution:

To contribute to the project, please create your own fork and merge the feature set. Ping a user from @Design Counsel if the<br> merge is rejected or not seen. <br>
The repo is located at https://github.com/BenBeehler/Ignis-Lang <br>
<br>
The language is built on top of Java for cross-platform support and easy rapid development. <br>
<br>
<br>
<br>
<h3>The Language:</h3>
<br>
The language is relatively simplistic in comparison to others.<br>
Modules are implemented for organization. They are declared as such:<br>
```elixir
def module <name>
    #body
end
```
<br>
<br>
Functions take inputs and produce outputs (with or without side effects)<br>
Functions left open in modules assume the name of <module_name>.<function_name><br>
Use the ``auto`` keyword for the function to be called at runtime.<br>
``:`` separates the declaration and the parameters<br>
If no parameters are present, it is necessary that the programmer leaves void<br>
```elixir
def fn Add: decimal one, decimal two
    Return one + two
end
```
<br>
Valid native types are:<br>
``object``<br>
``string``<br>
``int``<br>
``decimal``<br>
``tuple``<br>
``bool``<br>
``hash``<br>
<br>
Variable declarations are called in this format
Variables declared open inside of modules assume the name of <module_name>.<variable_name>
```elixir
<type> <name> = <value (function, raw, another variable, or another expression)>
```
<br>
<br>
Category declarations are such:<br>
``category <name> <parameter count>``<br>
<br>
Category-function additions are as such:<br>
``<category> => <function>``<br>
<br>
Category calls are as such:<br>
``<category> [parameters]``<br>

<h3>Notes:</h3>
- HTTP is handled by Jetty - a simple http server created by the eclipse team<br>
<br>
Dependencies<br>
- Jetty<br>
- JSoup<br>
- Java 1.8.144<br>
<br>
Any merges implementing a version higher than Java 1.8 will be rejected(edited)<br>
