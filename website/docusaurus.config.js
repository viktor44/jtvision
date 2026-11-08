// @ts-check
import { themes as prismThemes } from 'prism-react-renderer';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'JT Vision',
  tagline: 'A Java text-based UI framework inspired by Turbo Vision',
  url: 'https://jtvision.viktor44.org',
  baseUrl: '/',
  organizationName: 'viktor44',
  projectName: 'jtvision',
  favicon: 'img/favicon.svg',
  trailingSlash: true,
  onBrokenLinks: 'throw',

  markdown: {
    // *.md files are plain CommonMark (the generated API pages contain raw HTML from javadoc),
    // only *.mdx files are parsed as MDX.
    format: 'detect',
    hooks: { onBrokenMarkdownLinks: 'warn' },
  },

  i18n: { defaultLocale: 'en', locales: ['en'] },

  presets: [
    [
      '@docusaurus/preset-classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      {
        docs: {
          routeBasePath: '/',
          sidebarPath: './sidebars.js',
          editUrl: 'https://github.com/viktor44/jtvision/tree/main/website/',
        },
        blog: false,
        theme: { customCss: './src/css/custom.css' },
        googleTagManager: {
          containerId: 'GTM-T82BK7Q5',
        },
        gtag: {
          trackingID: 'G-Y18T9JV7G7',
          anonymizeIP: true,
        },
      },
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'JT Vision',
        items: [
          { type: 'docSidebar', sidebarId: 'guide', position: 'left', label: 'Guide' },
          { type: 'docSidebar', sidebarId: 'api', position: 'left', label: 'API Reference' },
          { href: 'https://github.com/viktor44/jtvision', label: 'GitHub', position: 'right' },
        ],
      },
      footer: {
        style: 'dark',
        copyright: 'Copyright © 2026 Viktor44. Apache License 2.0.',
      },
      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
        additionalLanguages: ['java'],
      },
    }),
};

export default config;
