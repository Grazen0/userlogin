# FAQ

Frequently asked questions

<small>[Go back](/{{ site.github.repository_name }}/en)</small>

<hr />

## Q: I changed the config, but no changes are taking effect!

A: Make sure to use the `/ul reload` command for config changes to take effect. If that doesn't work, check the server console for any errors.

## Q: The movement restriction isn't working!

A: Check your `config.yml` file. A common mistake is to put the following:

```yaml
restrictions:
  # ...
  movement: true # <-- THIS IS AN ERROR! You can't put values on section names!
    enabled: false
    warnFrequency: 20
  # ...
```

If your `restrictions` section looks like that, fix it by changing it to the following:

```yaml
restrictions:
  # ...
  movement: # <-- Remove any value you might have put here
    enabled: true # <-- Set this to true instead
    warnFrequency: 20
  # ...
```
