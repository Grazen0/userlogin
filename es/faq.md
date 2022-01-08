# FAQ

Preguntas frecuentes

<small>[Regresar](/{{ site.github.repository_name }}/es)</small>

<hr />

## #1: Edité el config, pero los cambios no tienen efecto!

Asegúrate de usar el comando `/ul reload` para recargar el plugin y que los cambios en el config tengan efecto. Si esto no funciona, busca errores en la consola del servidor.

## #2: La restricción de movimiento no funciona!

Revisa tu archivo `config.yml`. Un error frecuente es poner lo siguiente:

```yaml
restrictions:
  # ...
  movement: true # <-- ESTO ES UN ERROR! No puedes poner valores a nombres de secciones!
    enabled: false
    warnFrequency: 20
  # ...
```

Si tu sección de `restrictions` se ve así, arréglala cambiándola por lo siguiente:

```yaml
restrictions:
  # ...
  movement: # <-- Quita cualquier valor que hayas puesto aquí
    enabled: true # <-- Cambia esto a "true" en su lugar
    warnFrequency: 20
  # ...
```
