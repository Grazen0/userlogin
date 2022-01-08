# FAQ

Preguntas frecuentes

<small>[Regresar](/{{ site.github.repository_name }}/es)</small>

<hr />

## Edité el config, pero los cambios no tienen efecto

Asegúrate de usar el comando `/ul reload` para recargar el plugin y que los cambios en el config tengan efecto. Si esto no funciona, busca errores en la consola del servidor.

## ¿Cómo puedo hacer que los jugadores regresen a su posición anterior al logearse?

Activa `teleports.savePosition` en el config.

```yaml
teleports:
  # ...
  savePosition: true
  # ...
```

## La restricción de movimiento no funciona!

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

## ¿Cómo puedo hacer que jugadores premium se auto-logeen?

UserLogin tiene soporte para el plugin [FastLogin](https://www.spigotmc.org/resources/fastlogin.14153/), el cual permite la función de auto-logeo para jugadores premium. Para habilitar esto, basta con añadirlo a tus plugins. Cabe destacar que FastLogin no publica actualizaciones directamente a Spigot desde hace 4 años, sino que emplea otra plataforma. Puedes descargar la última versión [aquí](https://ci.codemc.io/job/Games647/job/FastLogin/).

## Los jugadores tienen que logearse nuevamente tras usar el comando de Spigot `/reload`!

Esto es un fallo conocido, y no hay nada que se pueda hacer al respecto. El comando `/reload` [causa diversos errores en el servidor, como pérdidas de memoria, algunos conflictos para algunos plugins, e inestabilidades](https://madelinemiller.dev/blog/problem-with-reload/). Si quieres recargar el plugin, usa `/ul reload` en su lugar.

## ¿Cómo configuro el plugin para BungeeCord?

Cabe destacar que existe una diferencia entre plugins de Spigot/Bukkit y BungeeCord. UserLogin es un plugin de Spigot, por lo que **lo tienes que colocar en los plugins de algún servidor de tu red, no en los plugins de la network**. Como referencia, deberías colocarlo en el servidor donde quieres que los usuarios se logeen.

Para enviar jugadores a otro servidor al logearse, busca la sección `bungeeCord` de tu `config.yml`:

```yaml
bungeeCord:
  enabled: true
  spawnServer: '[nombre del servidor de destino]'
  # ...
```
