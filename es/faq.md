# FAQ

Preguntas frecuentes

<small>[Regresar](/{{ site.github.repository_name }}/en)</small>

<hr />

## Q: Edité el config, pero los cambios no tienen efecto!

A: Asegúrate de usar `/ul reload` command for config changes to take effect. If that doesn't work, check the server console for any errors.
A: Asegúrate de usar el comando `/ul reload` para recargar el plugin y que los cambios en el config tengan efecto. Si esto no funciona, busca errores en la consola del servidor.

## Q: La restricción de movimiento no funciona!

A: Revisa tu archivo `config.yml`. Un error frecuente es poner lo siguiente:

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
